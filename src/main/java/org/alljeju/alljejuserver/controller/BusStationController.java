package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusStation;
import org.alljeju.alljejuserver.service.BusStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusStationController {

    private final BusStationService busStationService;
    
    /**
     * 버스 정류소 정보 수동 갱신
     */
    @GetMapping("/stations/refresh")
    public ResponseEntity<Map<String, String>> refreshBusStations() {
        log.info("버스 정류소 정보 수동 갱신 요청");
        busStationService.fetchAndSaveBusStations();
        return ResponseEntity.ok(Map.of("status", "success", "message", "버스 정류소 정보가 갱신되었습니다."));
    }
    
    /**
     * 최근 버스 정류소 정보 조회
     */
    @GetMapping("/stations")
    public ResponseEntity<List<BusStation>> getLatestBusStations(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        log.info("최근 버스 정류소 정보 조회 요청: limit={}", limit);
        List<BusStation> busStations = busStationService.getLatestBusStations(limit);
        return ResponseEntity.ok(busStations);
    }
    
    /**
     * 특정 정류소 정보 조회
     */
    @GetMapping("/stations/{stationId}")
    public ResponseEntity<BusStation> getBusStationByStationId(@PathVariable String stationId) {
        log.info("특정 정류소 정보 조회 요청: stationId={}", stationId);
        BusStation busStation = busStationService.getBusStationByStationId(stationId);
        return ResponseEntity.ok(busStation);
    }
    
    /**
     * 정류소 이름으로 검색
     */
    @GetMapping("/stations/search")
    public ResponseEntity<List<BusStation>> searchBusStationsByName(
            @RequestParam String keyword,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("정류소 이름 검색 요청: keyword={}, limit={}", keyword, limit);
        List<BusStation> busStations = busStationService.searchBusStationsByName(keyword, limit);
        return ResponseEntity.ok(busStations);
    }
}
