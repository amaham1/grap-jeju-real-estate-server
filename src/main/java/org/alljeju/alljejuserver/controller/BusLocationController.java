package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusLocation;
import org.alljeju.alljejuserver.service.BusLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusLocationController {

    private final BusLocationService busLocationService;
    
    /**
     * 버스 위치 정보 수동 갱신
     */
    @PostMapping("/locations/refresh")
    public ResponseEntity<Map<String, String>> refreshBusLocations() {
        log.info("버스 위치 정보 수동 갱신 요청");
        busLocationService.fetchAndSaveBusLocations();
        return ResponseEntity.ok(Map.of("status", "success", "message", "버스 위치 정보가 갱신되었습니다."));
    }
    
    /**
     * 최근 버스 위치 정보 조회
     */
    @GetMapping("/locations")
    public ResponseEntity<List<BusLocation>> getLatestBusLocations(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        log.info("최근 버스 위치 정보 조회 요청: limit={}", limit);
        List<BusLocation> busLocations = busLocationService.getLatestBusLocations(limit);
        return ResponseEntity.ok(busLocations);
    }
    
    /**
     * 특정 노선의 버스 위치 정보 조회
     */
    // @GetMapping("/locations/routes/{routeId}")
    // public ResponseEntity<List<BusLocation>> getBusLocationsByRouteId(@PathVariable String routeId) {
    //     log.info("특정 노선 버스 위치 정보 조회 요청: routeId={}", routeId);
    //     List<BusLocation> busLocations = busLocationService.getBusLocationsByRouteId(routeId);
    //     return ResponseEntity.ok(busLocations);
    // }
    
    /**
     * 버스 위치 정보 자동 갱신 (5분 간격)
     */
    // @Scheduled(fixedRate = 300000) // 5분(300초) 간격
    // public void scheduledRefreshBusLocations() {
    //     log.info("버스 위치 정보 자동 갱신 스케줄 실행");
    //     busLocationService.fetchAndSaveBusLocations();
    // }
}