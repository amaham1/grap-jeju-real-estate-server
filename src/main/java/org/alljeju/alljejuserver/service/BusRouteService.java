package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.dto.BusRouteApiResponse;
import org.alljeju.alljejuserver.mapper.BusRouteMapper;
import org.alljeju.alljejuserver.model.BusRoute;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusRouteService {

    private final BusRouteMapper busRouteMapper;
    
    @Value("${api.bus.location.key}")
    private String apiKey;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void fetchAndSaveBusRoutes() {
        try {
            log.info("버스 노선 정보 조회 API 호출 시작");
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/Bus";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=1&numOfRows=100";
            
            log.info("API 요청 URL: {}", fullUrl);
            
            // RestTemplate 사용하여 XML 응답 직접 받기
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            log.info("API 응답 상태 코드: {}", response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Jackson XML 매퍼를 사용하여 수동으로 XML 문자열을 객체로 변환
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    BusRouteApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusRouteApiResponse.class);
                    
                    if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                        List<BusRoute> busRoutes = convertToBusRoutes(apiResponse);
                        
                        if (!busRoutes.isEmpty()) {
                            busRouteMapper.batchInsert(busRoutes);
                            log.info("버스 노선 정보 저장 완료: {} 건", busRoutes.size());
                        } else {
                            log.warn("조회된 버스 노선 정보가 없습니다.");
                        }
                        
                        // 데이터가 많을 경우 페이징 처리
                        if (apiResponse.getBody().getTotalCount() > 0) {
                            int totalPages = (apiResponse.getBody().getTotalCount() + apiResponse.getBody().getNumOfRows() - 1) / apiResponse.getBody().getNumOfRows();
                            
                            if (totalPages > 1) {
                                for (int page = 2; page <= totalPages; page++) {
                                    fetchAndSaveBusRoutePage(page, apiResponse.getBody().getNumOfRows());
                                }
                            }
                        }
                    } else {
                        log.error("버스 노선 정보 조회 API 에러: {}", 
                            apiResponse != null ? apiResponse.getHeader().getResultMsg() : "응답 처리 실패");
                    }
                } catch (Exception e) {
                    log.error("XML 파싱 중 오류 발생", e);
                    throw new RuntimeException("XML 파싱 중 오류 발생", e);
                }
            } else {
                log.error("API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("API 호출 실패: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("버스 노선 정보 조회 중 오류 발생", e);
            throw new RuntimeException("버스 노선 정보 조회 중 오류 발생", e);
        }
    }
    
    private void fetchAndSaveBusRoutePage(int pageNo, int numOfRows) {
        try {
            log.info("버스 노선 정보 페이지 {} 조회", pageNo);
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/Bus";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                XmlMapper xmlMapper = new XmlMapper();
                BusRouteApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusRouteApiResponse.class);
                
                if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                    List<BusRoute> busRoutes = convertToBusRoutes(apiResponse);
                    if (!busRoutes.isEmpty()) {
                        busRouteMapper.batchInsert(busRoutes);
                        log.info("버스 노선 정보 페이지 {} 저장 완료: {} 건", pageNo, busRoutes.size());
                    }
                }
            }
        } catch (Exception e) {
            log.error("버스 노선 정보 페이지 {} 조회 중 오류 발생", pageNo, e);
        }
    }
    
    private List<BusRoute> convertToBusRoutes(BusRouteApiResponse response) {
        if (response.getBody() == null || response.getBody().getItems() == null || 
            response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        
        return response.getBody().getItems().getItemList().stream()
                .map(item -> {
                    BusRoute route = new BusRoute();
                    route.setRouteId(item.getRouteId());
                    route.setRouteNm(item.getRouteNm());
                    route.setRouteNum(item.getRouteNum());
                    route.setRouteSubNm(item.getRouteSubNm());
                    route.setRouteTp(item.getRouteTp());
                    route.setRouteColor(item.getRouteColor());
                    route.setOrgtStationId(item.getOrgtStationId());
                    route.setDstStationId(item.getDstStationId());
                    route.setGovNm(item.getGovNm());
                    route.setUseYn(item.getUseYn());
                    
                    try {
                        if (item.getRouteLen() != null && !item.getRouteLen().isEmpty()) {
                            route.setRouteLen(Integer.parseInt(item.getRouteLen()));
                        }
                        
                        if (item.getStationCnt() != null && !item.getStationCnt().isEmpty()) {
                            route.setStationCnt(Integer.parseInt(item.getStationCnt()));
                        }
                    } catch (NumberFormatException e) {
                        log.warn("숫자 변환 중 오류: {}", e.getMessage());
                    }
                    
                    try {
                        if (item.getUpd() != null && !item.getUpd().isEmpty()) {
                            route.setUpd(LocalDateTime.parse(item.getUpd(), DATE_FORMATTER));
                        }
                    } catch (Exception e) {
                        log.warn("날짜 변환 중 오류: {}", e.getMessage());
                    }
                    
                    return route;
                })
                .collect(Collectors.toList());
    }
    
    public List<BusRoute> getLatestBusRoutes(int limit) {
        return busRouteMapper.findLatest(limit);
    }
    
    public BusRoute getBusRouteByRouteId(String routeId) {
        return busRouteMapper.findByRouteId(routeId);
    }
    
    public List<BusRoute> searchBusRoutesByKeyword(String keyword, int limit) {
        return busRouteMapper.searchByKeyword(keyword, limit);
    }
    
    public List<BusRoute> getActiveBusRoutes() {
        return busRouteMapper.findActiveRoutes();
    }
    
    public List<BusRoute> getBusRoutesByRouteNum(String routeNum) {
        return busRouteMapper.findByRouteNum(routeNum);
    }
}
