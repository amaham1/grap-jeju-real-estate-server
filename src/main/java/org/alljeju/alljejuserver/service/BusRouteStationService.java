package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.dto.BusRouteStationApiResponse;
import org.alljeju.alljejuserver.mapper.BusRouteStationMapper;
import org.alljeju.alljejuserver.model.BusRouteStation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusRouteStationService {

    private final BusRouteStationMapper busRouteStationMapper;
    
    @Value("${api.bus.location.key}")
    private String apiKey;
    
    @Value("${api.bus.location.url}")
    private String apiBaseUrl;
    
    // API 요청 간 지연 시간 (밀리초)
    @Value("${api.request.delay:3000}")
    private int requestDelayMs;
    
    // API 요청 재시도 횟수
    @Value("${api.request.retry:3}")
    private int requestRetryCount;
    
    // 배치 크기 - 한 번에 처리할 노선 수
    @Value("${api.request.batch.size:10}")
    private int batchSize;
    
    public int fetchAndSaveAllBusRouteStations() {
        List<String> routeIds = busRouteStationMapper.findAllRouteIds();
        log.info("총 {} 개의 버스 노선 정보를 조회합니다.", routeIds.size());
        
        if (routeIds.isEmpty()) {
            log.warn("조회할 버스 노선이 없습니다. 먼저 버스 노선 정보를 등록해주세요.");
            return 0;
        }
        
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        int totalProcessed = 0;
        
        log.info("API 요청 간 지연 시간: {}ms, 재시도 횟수: {}, 배치 크기: {}", 
                requestDelayMs, requestRetryCount, batchSize);
        
        // 각 노선별로 정류장 정보 조회
        for (int i = 0; i < routeIds.size(); i++) {
            String routeId = routeIds.get(i);
            try {
                int count = fetchAndSaveBusRouteStations(routeId);
                if (count > 0) {
                    successCount.addAndGet(count);
                    log.info("노선 ID: {}에 대한 {} 개 정류장 정보 저장 완료", routeId, count);
                } else {
                    failCount.incrementAndGet();
                    log.warn("노선 ID: {}에 대한 정류장 정보가 없습니다.", routeId);
                }
                
                totalProcessed++;
                
                // 배치 크기마다 더 긴 대기 시간 추가
                if (totalProcessed % batchSize == 0 && i < routeIds.size() - 1) {
                    int batchPauseTime = requestDelayMs * 1;
                    log.info("배치 처리 완료 ({}/{}), 다음 배치 전 {}ms 대기", 
                            totalProcessed, routeIds.size(), batchPauseTime);
                    Thread.sleep(batchPauseTime);
                }
                
            } catch (Exception e) {
                failCount.incrementAndGet();
                log.error("노선 ID: {}에 대한 정류장 정보 조회 중 오류 발생: {}", routeId, e.getMessage());
                
                try {
                    // 오류 발생 시 더 긴 대기 시간 적용
                    int errorPauseTime = requestDelayMs * 3;
                    log.info("오류 발생 후 {}ms 대기", errorPauseTime);
                    Thread.sleep(errorPauseTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                
                // 오류가 발생해도 계속 진행
                continue;
            }
        }
        
        log.info("전체 {} 개 노선 중 {} 개 성공, {} 개 실패, 총 {} 개 정류장 정보 갱신 완료", 
                routeIds.size(), 
                routeIds.size() - failCount.get(), 
                failCount.get(), 
                successCount.get());
        
        return successCount.get();
    }
    
    public int fetchAndSaveBusRouteStations(String routeId) {
        int retryCount = 0;
        Exception lastException = null;
        
        while (retryCount < requestRetryCount) {
            try {
                log.info("노선 ID: {}에 대한 정류장 정보 조회 시작 (시도: {}/{})", 
                        routeId, retryCount + 1, requestRetryCount);
                
                String requestUrl = apiBaseUrl + "/BusLocation";
                String fullUrl = requestUrl + "?serviceKey=" + apiKey + "&route=" + routeId;
                
                log.debug("API 요청 URL: {}", fullUrl);
                
                ResponseEntity<String> response = new RestTemplate().getForEntity(fullUrl, String.class);
                
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    XmlMapper xmlMapper = new XmlMapper();
                    BusRouteStationApiResponse apiResponse = xmlMapper.readValue(response.getBody(), BusRouteStationApiResponse.class);
                    
                    if (apiResponse != null && "00".equals(apiResponse.getHeader().getResultCode())) {
                        List<BusRouteStation> busRouteStations = convertToBusRouteStations(apiResponse);
                        
                        if (!busRouteStations.isEmpty()) {
                            busRouteStationMapper.batchInsert(busRouteStations);
                            log.info("노선 ID: {}에 대한 {} 개 정류장 정보 저장 완료", routeId, busRouteStations.size());
                            return busRouteStations.size();
                        } else {
                            log.warn("노선 ID: {}에 대한 정류장 정보가 없습니다.", routeId);
                            return 0;
                        }
                    } else {
                        String resultMsg = apiResponse != null ? apiResponse.getHeader().getResultMsg() : "응답 처리 실패";
                        log.error("노선 ID: {}에 대한 정류장 정보 조회 API 에러: {}", routeId, resultMsg);
                        
                        // 서비스 정상 응답이지만 결과 코드가 오류인 경우 재시도하지 않음
                        if (apiResponse != null && !"99".equals(apiResponse.getHeader().getResultCode())) {
                            return 0;
                        }
                    }
                } else {
                    log.error("API 호출 실패: {}", response.getStatusCode());
                }
            } catch (HttpClientErrorException e) {
                lastException = e;
                log.error("API 호출 중 클라이언트 오류 (노선 ID: {}): {} - {}", 
                        routeId, e.getStatusCode(), e.getResponseBodyAsString());
                
                // 401, 403 등의 인증 관련 오류는 재시도하지 않음
                if (e.getStatusCode().value() == 401 || e.getStatusCode().value() == 403) {
                    log.error("인증 오류로 재시도하지 않습니다.");
                    break;
                }
            } catch (HttpServerErrorException e) {
                lastException = e;
                log.error("API 호출 중 서버 오류 (노선 ID: {}): {} - {}", 
                        routeId, e.getStatusCode(), e.getResponseBodyAsString());
                
                // 서버 오류(5xx)는 재시도
            } catch (ResourceAccessException e) {
                lastException = e;
                log.error("API 서버 연결 오류 (노선 ID: {}): {}", routeId, e.getMessage());
                
                // 네트워크 오류는 재시도
            } catch (Exception e) {
                lastException = e;
                log.error("API 응답 처리 중 오류 (노선 ID: {}): {}", routeId, e.getMessage());
            }
            
            // 재시도 전 대기
            retryCount++;
            if (retryCount < requestRetryCount) {
                try {
                    int retryDelayMs = requestDelayMs * (retryCount + 1);
                    log.info("재시도 전 {}ms 대기 (시도: {}/{})", retryDelayMs, retryCount, requestRetryCount);
                    Thread.sleep(retryDelayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        if (retryCount >= requestRetryCount && lastException != null) {
            log.error("노선 ID: {}에 대한 정류장 정보 조회 최대 재시도 횟수 초과", routeId);
        }
        
        return 0;
    }
    
    private List<BusRouteStation> convertToBusRouteStations(BusRouteStationApiResponse response) {
        if (response.getBody() == null || response.getBody().getItems() == null || 
            response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        
        return response.getBody().getItems().getItemList().stream()
                .map(item -> {
                    BusRouteStation station = new BusRouteStation();
                    station.setRouteId(item.getRouteId());
                    station.setRouteNm(item.getRouteNm());
                    station.setRouteSubNm(item.getRouteSubNm());
                    station.setRouteTp(item.getRouteTp());
                    station.setStationId(item.getStationId());
                    station.setStationNm(item.getStationNm());
                    station.setUpdnDir(item.getUpdnDir());
                    station.setLowPlateTp(item.getLowPlateTp());
                    station.setPlateNo(item.getPlateNo());
                    
                    try {
                        if (item.getLocalX() != null && !item.getLocalX().equalsIgnoreCase("null") && !item.getLocalX().isEmpty()) {
                            station.setLocalX(Double.parseDouble(item.getLocalX()));
                        }
                        
                        if (item.getLocalY() != null && !item.getLocalY().equalsIgnoreCase("null") && !item.getLocalY().isEmpty()) {
                            station.setLocalY(Double.parseDouble(item.getLocalY()));
                        }
                        
                        if (item.getMobiNum() != null && !item.getMobiNum().equalsIgnoreCase("null") && !item.getMobiNum().isEmpty()) {
                            station.setMobiNum(Integer.parseInt(item.getMobiNum()));
                        }
                        
                        if (item.getStationOrd() != null && !item.getStationOrd().equalsIgnoreCase("null") && !item.getStationOrd().isEmpty()) {
                            station.setStationOrd(Integer.parseInt(item.getStationOrd()));
                        }
                    } catch (NumberFormatException e) {
                        log.warn("숫자 변환 중 오류: {}", e.getMessage());
                    }
                    
                    return station;
                })
                .collect(Collectors.toList());
    }
    
    public List<BusRouteStation> getBusRouteStationsByRouteId(String routeId) {
        return busRouteStationMapper.findByRouteId(routeId);
    }
    
    public List<BusRouteStation> getBusRouteStationsByStationId(String stationId) {
        return busRouteStationMapper.findByStationId(stationId);
    }
    
    public List<BusRouteStation> searchBusRouteStationsByKeyword(String keyword, int limit) {
        return busRouteStationMapper.searchByKeyword(keyword, limit);
    }
}
