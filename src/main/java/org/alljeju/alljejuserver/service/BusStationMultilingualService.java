package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.dto.BusStationMultilingualApiResponse;
import org.alljeju.alljejuserver.mapper.BusStationMultilingualMapper;
import org.alljeju.alljejuserver.model.BusStationMultilingual;
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
public class BusStationMultilingualService {

    private final BusStationMultilingualMapper busStationMultilingualMapper;
    
    @Value("${api.bus.location.key}")
    private String apiKey;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void fetchAndSaveBusStationsMultilingual() {
        try {
            log.info("다국어 버스 정류소 정보 조회 API 호출 시작");
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/Station2";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=1&numOfRows=100";
            
            log.info("API 요청 URL: {}", fullUrl);
            
            // RestTemplate 사용하여 XML 응답 직접 받기
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            log.info("API 응답 상태 코드: {}", response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Jackson XML 매퍼를 사용하여 수동으로 XML 문자열을 객체로 변환
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    BusStationMultilingualApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusStationMultilingualApiResponse.class);
                    
                    if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                        List<BusStationMultilingual> busStations = convertToBusStationsMultilingual(apiResponse);
                        
                        if (!busStations.isEmpty()) {
                            busStationMultilingualMapper.batchInsert(busStations);
                            log.info("다국어 버스 정류소 정보 저장 완료: {} 건", busStations.size());
                        } else {
                            log.warn("조회된 다국어 버스 정류소 정보가 없습니다.");
                        }
                        
                        // 데이터가 많을 경우 페이징 처리
                        int totalPages = (apiResponse.getBody().getTotalCount() + apiResponse.getBody().getNumOfRows() - 1) / apiResponse.getBody().getNumOfRows();
                        
                        if (totalPages > 1) {
                            for (int page = 2; page <= totalPages; page++) {
                                fetchAndSaveBusStationsMultilingualPage(page, apiResponse.getBody().getNumOfRows());
                            }
                        }
                    } else {
                        log.error("다국어 버스 정류소 정보 조회 API 에러: {}", 
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
            log.error("다국어 버스 정류소 정보 조회 중 오류 발생", e);
            throw new RuntimeException("다국어 버스 정류소 정보 조회 중 오류 발생", e);
        }
    }
    
    private void fetchAndSaveBusStationsMultilingualPage(int pageNo, int numOfRows) {
        try {
            log.info("다국어 버스 정류소 정보 페이지 {} 조회", pageNo);
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/Station2";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=" + pageNo + "&numOfRows=" + numOfRows;
            
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                XmlMapper xmlMapper = new XmlMapper();
                BusStationMultilingualApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusStationMultilingualApiResponse.class);
                
                if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                    List<BusStationMultilingual> busStations = convertToBusStationsMultilingual(apiResponse);
                    if (!busStations.isEmpty()) {
                        busStationMultilingualMapper.batchInsert(busStations);
                        log.info("다국어 버스 정류소 정보 페이지 {} 저장 완료: {} 건", pageNo, busStations.size());
                    }
                }
            }
        } catch (Exception e) {
            log.error("다국어 버스 정류소 정보 페이지 {} 조회 중 오류 발생", pageNo, e);
        }
    }
    
    private List<BusStationMultilingual> convertToBusStationsMultilingual(BusStationMultilingualApiResponse response) {
        if (response.getBody() == null || response.getBody().getItems() == null || 
            response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        
        return response.getBody().getItems().getItemList().stream()
                .map(item -> {
                    BusStationMultilingual station = new BusStationMultilingual();
                    station.setStationId(item.getStationId());
                    station.setStationNm(item.getStationNm());
                    station.setStationNmEn(item.getStationNmEn());
                    station.setStationNmCh(item.getStationNmCh());
                    station.setStationNmJp(item.getStationNmJp());
                    station.setGovNm(item.getGovNm());
                    station.setDirTp(item.getDirTp());
                    station.setUseYn(item.getUseYn());
                    
                    try {
                        if (item.getLocalX() != null && !item.getLocalX().isEmpty()) {
                            station.setLocalX(Double.parseDouble(item.getLocalX()));
                        }
                        
                        if (item.getLocalY() != null && !item.getLocalY().isEmpty()) {
                            station.setLocalY(Double.parseDouble(item.getLocalY()));
                        }
                        
                        if (item.getMobiNum() != null && !item.getMobiNum().isEmpty()) {
                            station.setMobiNum(Integer.parseInt(item.getMobiNum()));
                        }
                    } catch (NumberFormatException e) {
                        log.warn("숫자 변환 중 오류: {}", e.getMessage());
                    }
                    
                    try {
                        if (item.getUpd() != null && !item.getUpd().isEmpty()) {
                            station.setUpd(LocalDateTime.parse(item.getUpd(), DATE_FORMATTER));
                        }
                    } catch (Exception e) {
                        log.warn("날짜 변환 중 오류: {}", e.getMessage());
                    }
                    
                    return station;
                })
                .collect(Collectors.toList());
    }
    
    public List<BusStationMultilingual> getLatestBusStationsMultilingual(int limit) {
        return busStationMultilingualMapper.findLatest(limit);
    }
    
    public BusStationMultilingual getBusStationMultilingualByStationId(String stationId) {
        return busStationMultilingualMapper.findByStationId(stationId);
    }
    
    public List<BusStationMultilingual> searchBusStationsMultilingualByKeyword(String keyword, int limit) {
        return busStationMultilingualMapper.searchByKeyword(keyword, limit);
    }
    
    public List<BusStationMultilingual> getActiveBusStationsMultilingual() {
        return busStationMultilingualMapper.findActiveStations();
    }
}
