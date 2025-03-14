package org.alljeju.alljejuserver.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusLocation;
import org.alljeju.alljejuserver.dto.BusLocationApiResponse;
import org.alljeju.alljejuserver.mapper.BusLocationMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusLocationService {

    private final BusLocationMapper busLocationMapper;
    private final WebClient webClient;
    
    @Value("${api.bus.location.url}")
    private String busLocationApiUrl;
    
    @Value("${api.bus.location.key}")
    private String apiKey;
    
    public void fetchAndSaveBusLocations() {
        try {
            log.info("버스 위치 정보 조회 API 호출 시작");
            
            BusLocationApiResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(busLocationApiUrl + "/BusLocation?route=")
                            .queryParam("serviceKey", apiKey)
                            .queryParam("pageNo", 1)
                            .queryParam("numOfRows", 100)
                            .build())
                    .accept(MediaType.APPLICATION_XML)
                    .retrieve()
                    .bodyToMono(BusLocationApiResponse.class)
                    .block();
            
            if (response != null && "00".equals(response.getHeader().getResultCode())) {
                List<BusLocation> busLocations = convertToBusLocations(response);
                if (!busLocations.isEmpty()) {
                    busLocationMapper.batchInsert(busLocations);
                    log.info("버스 위치 정보 저장 완료: {} 건", busLocations.size());
                } else {
                    log.warn("조회된 버스 위치 정보가 없습니다.");
                }
            } else {
                log.error("버스 위치 정보 조회 API 에러: {}", 
                    response != null ? response.getHeader().getResultMsg() : "응답이 없습니다.");
            }
        } catch (Exception e) {
            log.error("버스 위치 정보 조회 중 오류 발생", e);
            throw new RuntimeException("버스 위치 정보 조회 중 오류 발생", e);
        }
    }
    
    private List<BusLocation> convertToBusLocations(BusLocationApiResponse response) {
        if (response.getBody() == null || response.getBody().getItems() == null || 
            response.getBody().getItems().getItemList() == null) {
            return new ArrayList<>();
        }
        
        return response.getBody().getItems().getItemList().stream()
                .map(item -> {
                    BusLocation busLocation = new BusLocation();
                    try {
                        busLocation.setLocalX(item.getLocalX() != null ? new BigDecimal(item.getLocalX()) : null);
                        busLocation.setLocalY(item.getLocalY() != null ? new BigDecimal(item.getLocalY()) : null);
                    } catch (NumberFormatException e) {
                        log.warn("위치 좌표 변환 중 오류: {}", e.getMessage());
                    }
                    busLocation.setLowPlateTp(item.getLowPlateTp());
                    busLocation.setMobiNum(item.getMobiNum());
                    busLocation.setPlateNo(item.getPlateNo());
                    busLocation.setRouteId(item.getRouteId());
                    busLocation.setRouteNm(item.getRouteNm());
                    busLocation.setRouteSubNm(item.getRouteSubNm());
                    busLocation.setRouteTp(item.getRouteTp());
                    busLocation.setStationId(item.getStationId());
                    busLocation.setStationNm(item.getStationNm());
                    try {
                        busLocation.setStationOrd(item.getStationOrd() != null ? Integer.parseInt(item.getStationOrd()) : null);
                    } catch (NumberFormatException e) {
                        log.warn("정류소 순번 변환 중 오류: {}", e.getMessage());
                    }
                    busLocation.setUpdnDir(item.getUpdnDir());
                    return busLocation;
                })
                .collect(Collectors.toList());
    }
    
    public List<BusLocation> getLatestBusLocations(int limit) {
        return busLocationMapper.findLatest(limit);
    }
    
    public List<BusLocation> getBusLocationsByRouteId(String routeId) {
        return busLocationMapper.findByRouteId(routeId);
    }
}