package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusRouteStation;
import org.alljeju.alljejuserver.service.BusRouteStationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusRouteStationController {

    private final BusRouteStationService busRouteStationService;
    
    /**
     * 모든 노선의 정류장 정보 수동 갱신
     */
    @GetMapping("/route-stations/refresh")
    public ResponseEntity<Map<String, String>> refreshAllBusRouteStations() {
        log.info("모든 노선의 정류장 정보 수동 갱신 요청");
        busRouteStationService.fetchAndSaveAllBusRouteStations();
        return ResponseEntity.ok(Map.of("status", "success", "message", "모든 노선의 정류장 정보가 갱신되었습니다."));
    }
    
    /**
     * 특정 노선의 정류장 정보 수동 갱신
     */
    @GetMapping("/route-stations/refresh/{routeId}")
    public ResponseEntity<Map<String, String>> refreshBusRouteStationsByRouteId(@PathVariable String routeId) {
        log.info("노선 ID: {}에 대한 정류장 정보 수동 갱신 요청", routeId);
        int count = busRouteStationService.fetchAndSaveBusRouteStations(routeId);
        return ResponseEntity.ok(Map.of(
            "status", "success", 
            "message", "노선 ID: " + routeId + "에 대한 " + count + "개 정류장 정보가 갱신되었습니다."
        ));
    }
    
    /**
     * 특정 노선의 정류장 정보 조회
     */
    @GetMapping("/route-stations/routes/{routeId}")
    public ResponseEntity<List<BusRouteStation>> getBusRouteStationsByRouteId(@PathVariable String routeId) {
        log.info("노선 ID: {}에 대한 정류장 정보 조회 요청", routeId);
        List<BusRouteStation> stations = busRouteStationService.getBusRouteStationsByRouteId(routeId);
        return ResponseEntity.ok(stations);
    }
    
    /**
     * 특정 정류장의 노선 정보 조회
     */
    @GetMapping("/route-stations/stations/{stationId}")
    public ResponseEntity<List<BusRouteStation>> getBusRouteStationsByStationId(@PathVariable String stationId) {
        log.info("정류장 ID: {}에 대한 노선 정보 조회 요청", stationId);
        List<BusRouteStation> stations = busRouteStationService.getBusRouteStationsByStationId(stationId);
        return ResponseEntity.ok(stations);
    }
    
    /**
     * 키워드로 정류장 정보 검색
     */
    @GetMapping("/route-stations/search")
    public ResponseEntity<List<BusRouteStation>> searchBusRouteStationsByKeyword(
            @RequestParam String keyword,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("키워드로 정류장 정보 검색 요청: keyword={}, limit={}", keyword, limit);
        List<BusRouteStation> stations = busRouteStationService.searchBusRouteStationsByKeyword(keyword, limit);
        return ResponseEntity.ok(stations);
    }
}
