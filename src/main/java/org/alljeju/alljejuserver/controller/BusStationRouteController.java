package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusStationRoute;
import org.alljeju.alljejuserver.service.BusStationRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusStationRouteController {

    private final BusStationRouteService busStationRouteService;
    
    /**
     * 버스 노선별 정류소 정보 수동 갱신
     */
    @GetMapping("/stationRoutes/refresh")
    public ResponseEntity<Map<String, String>> refreshBusStationRoutes() {
        log.info("버스 노선별 정류소 정보 수동 갱신 요청");
        busStationRouteService.fetchAndSaveBusStationRoutes();
        return ResponseEntity.ok(Map.of("status", "success", "message", "버스 노선별 정류소 정보가 갱신되었습니다."));
    }
    
    /**
     * 최근 버스 노선별 정류소 정보 조회
     */
    @GetMapping("/stationRoutes")
    public ResponseEntity<List<BusStationRoute>> getLatestBusStationRoutes(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        log.info("최근 버스 노선별 정류소 정보 조회 요청: limit={}", limit);
        List<BusStationRoute> busStationRoutes = busStationRouteService.getLatestBusStationRoutes(limit);
        return ResponseEntity.ok(busStationRoutes);
    }
    
    /**
     * 특정 노선의 정류소 정보 조회
     */
    @GetMapping("/stationRoutes/routes/{routeId}")
    public ResponseEntity<List<BusStationRoute>> getBusStationRoutesByRouteId(@PathVariable String routeId) {
        log.info("특정 노선의 정류소 정보 조회 요청: routeId={}", routeId);
        List<BusStationRoute> busStationRoutes = busStationRouteService.getBusStationRoutesByRouteId(routeId);
        return ResponseEntity.ok(busStationRoutes);
    }
    
    /**
     * 특정 정류소의 노선 정보 조회
     */
    @GetMapping("/stationRoutes/stations/{stationId}")
    public ResponseEntity<List<BusStationRoute>> getBusStationRoutesByStationId(@PathVariable String stationId) {
        log.info("특정 정류소의 노선 정보 조회 요청: stationId={}", stationId);
        List<BusStationRoute> busStationRoutes = busStationRouteService.getBusStationRoutesByStationId(stationId);
        return ResponseEntity.ok(busStationRoutes);
    }
}
