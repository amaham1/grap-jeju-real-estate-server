package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusStation;
import org.alljeju.alljejuserver.dto.BusStationApiResponse;
import org.alljeju.alljejuserver.mapper.BusStationMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusStationService {

    private final BusStationMapper busStationMapper;
    
    @Value("${api.bus.location.key}")
    private String apiKey;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public void fetchAndSaveBusStations() {
        try {
            log.info("버스 정류소 정보 조회 API 호출 시작");
            
            String requestUrl = "http://busopen.jeju.go.kr/OpenAPI/service/bis/Station";
            String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&pageNo=1&numOfRows=100";
            
            log.info("API 요청 URL: {}", fullUrl);
            
            // RestTemplate 사용하여 XML 응답 직접 받기
            ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
            
            log.info("API 응답 상태 코드: {}", response.getStatusCode());
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Jackson XML 매퍼를 사용하여 수동으로 XML 문자열을 객체로 변환
                try {
                    XmlMapper xmlMapper = new XmlMapper();
                    BusStationApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusStationApiResponse.class);
                    
                    if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                        List<BusStation> busStations = convertToBusStations(apiResponse);
                        if (!busStations.isEmpty()) {
                            busStationMapper.batchInsert(busStations);
                            log.info("버스 정류소 정보 저장 완료: {} 건", busStations.size());
                        } else {
                            log.warn("조회된 버스 정류소 정보가 없습니다.");
                        }
                    } else {
                        log.error("버스 정류소 정보 조회 API 에러: {}", 
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
            log.error("버스 정류소 정보 조회 중 오류 발생", e);
            throw new RuntimeException("버스 정류소 정보 조회 중 오류 발생", e);
        }
    }
    
    private List<BusStation> convertToBusStations(BusStationApiResponse response) {
        if (response.getBody() == null || response.getBody().getItems() == null || 
            response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        
        return response.getBody().getItems().getItemList().stream()
                .map(item -> {
                    BusStation busStation = new BusStation();
                    busStation.setDirTp(item.getDirTp());
                    busStation.setGovNm(item.getGovNm());
                    try {
                        busStation.setLocalX(item.getLocalX() != null ? new BigDecimal(item.getLocalX()) : null);
                        busStation.setLocalY(item.getLocalY() != null ? new BigDecimal(item.getLocalY()) : null);
                    } catch (NumberFormatException e) {
                        log.warn("위치 좌표 변환 중 오류: {}", e.getMessage());
                    }
                    busStation.setMobiNum(item.getMobiNum());
                    busStation.setStationId(item.getStationId());
                    busStation.setStationNm(item.getStationNm());
                    try {
                        if (item.getUpd() != null && !item.getUpd().isEmpty()) {
                            busStation.setUpd(LocalDateTime.parse(item.getUpd(), DATE_FORMATTER));
                        }
                    } catch (Exception e) {
                        log.warn("날짜 변환 중 오류: {}", e.getMessage());
                    }
                    busStation.setUseYn(item.getUseYn());
                    return busStation;
                })
                .collect(Collectors.toList());
    }
    
    public List<BusStation> getLatestBusStations(int limit) {
        return busStationMapper.findLatest(limit);
    }
    
    public BusStation getBusStationByStationId(String stationId) {
        return busStationMapper.findByStationId(stationId);
    }
    
    public List<BusStation> searchBusStationsByName(String keyword, int limit) {
        return busStationMapper.findByStationName(keyword, limit);
    }
}
