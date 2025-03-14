package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusVehicle;
import org.alljeju.alljejuserver.service.BusVehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusVehicleController {

    private final BusVehicleService busVehicleService;
    
    /**
     * 버스 차량 정보 수동 갱신
     */
    @GetMapping("/vehicles/refresh")
    public ResponseEntity<Map<String, String>> refreshBusVehicles() {
        log.info("버스 차량 정보 수동 갱신 요청");
        busVehicleService.fetchAndSaveBusVehicles();
        return ResponseEntity.ok(Map.of("status", "success", "message", "버스 차량 정보가 갱신되었습니다."));
    }
    
    /**
     * 최근 버스 차량 정보 조회
     */
    @GetMapping("/vehicles")
    public ResponseEntity<List<BusVehicle>> getLatestBusVehicles(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        log.info("최근 버스 차량 정보 조회 요청: limit={}", limit);
        List<BusVehicle> busVehicles = busVehicleService.getLatestBusVehicles(limit);
        return ResponseEntity.ok(busVehicles);
    }
    
    /**
     * 특정 차량 ID로 버스 차량 정보 조회
     */
    @GetMapping("/vehicles/{vhId}")
    public ResponseEntity<BusVehicle> getBusVehicleByVehicleId(@PathVariable String vhId) {
        log.info("특정 차량 ID로 버스 차량 정보 조회 요청: vhId={}", vhId);
        BusVehicle busVehicle = busVehicleService.getBusVehicleByVehicleId(vhId);
        if (busVehicle == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(busVehicle);
    }
    
    /**
     * 차량 번호판으로 버스 차량 정보 검색
     */
    @GetMapping("/vehicles/search")
    public ResponseEntity<List<BusVehicle>> searchBusVehiclesByPlateNo(
            @RequestParam String keyword,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("차량 번호판으로 버스 차량 정보 검색 요청: keyword={}, limit={}", keyword, limit);
        List<BusVehicle> busVehicles = busVehicleService.searchBusVehiclesByPlateNo(keyword, limit);
        return ResponseEntity.ok(busVehicles);
    }
    
    /**
     * 운행 중인 버스 차량 정보 조회
     */
    @GetMapping("/vehicles/active")
    public ResponseEntity<List<BusVehicle>> getActiveBusVehicles() {
        log.info("운행 중인 버스 차량 정보 조회 요청");
        List<BusVehicle> busVehicles = busVehicleService.getActiveBusVehicles();
        return ResponseEntity.ok(busVehicles);
    }
}
