package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusRoute;
import org.alljeju.alljejuserver.service.BusRouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusRouteController {

    private final BusRouteService busRouteService;
    
    /**
     * 버스 노선 정보 수동 갱신
     */
    @GetMapping("/routes/refresh")
    public ResponseEntity<Map<String, String>> refreshBusRoutes() {
        log.info("버스 노선 정보 수동 갱신 요청");
        busRouteService.fetchAndSaveBusRoutes();
        return ResponseEntity.ok(Map.of("status", "success", "message", "버스 노선 정보가 갱신되었습니다."));
    }
    
    /**
     * 최근 버스 노선 정보 조회
     */
    @GetMapping("/routes")
    public ResponseEntity<List<BusRoute>> getLatestBusRoutes(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        log.info("최근 버스 노선 정보 조회 요청: limit={}", limit);
        List<BusRoute> busRoutes = busRouteService.getLatestBusRoutes(limit);
        return ResponseEntity.ok(busRoutes);
    }
    
    /**
     * 특정 노선 ID로 버스 노선 정보 조회
     */
    @GetMapping("/routes/{routeId}")
    public ResponseEntity<BusRoute> getBusRouteByRouteId(@PathVariable String routeId) {
        log.info("특정 노선 ID로 버스 노선 정보 조회 요청: routeId={}", routeId);
        BusRoute busRoute = busRouteService.getBusRouteByRouteId(routeId);
        if (busRoute == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(busRoute);
    }
    
    /**
     * 키워드로 버스 노선 정보 검색
     */
    @GetMapping("/routes/search")
    public ResponseEntity<List<BusRoute>> searchBusRoutesByKeyword(
            @RequestParam String keyword,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("키워드로 버스 노선 정보 검색 요청: keyword={}, limit={}", keyword, limit);
        List<BusRoute> busRoutes = busRouteService.searchBusRoutesByKeyword(keyword, limit);
        return ResponseEntity.ok(busRoutes);
    }
    
    /**
     * 사용 중인 버스 노선 정보 조회
     */
    @GetMapping("/routes/active")
    public ResponseEntity<List<BusRoute>> getActiveBusRoutes() {
        log.info("사용 중인 버스 노선 정보 조회 요청");
        List<BusRoute> busRoutes = busRouteService.getActiveBusRoutes();
        return ResponseEntity.ok(busRoutes);
    }
    
    /**
     * 특정 노선 번호로 버스 노선 정보 조회
     */
    @GetMapping("/routes/numbers/{routeNum}")
    public ResponseEntity<List<BusRoute>> getBusRoutesByRouteNum(@PathVariable String routeNum) {
        log.info("특정 노선 번호로 버스 노선 정보 조회 요청: routeNum={}", routeNum);
        List<BusRoute> busRoutes = busRouteService.getBusRoutesByRouteNum(routeNum);
        return ResponseEntity.ok(busRoutes);
    }
}
