package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alljeju.alljejuserver.model.BusStationMultilingual;
import org.alljeju.alljejuserver.service.BusStationMultilingualService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/bus")
@RequiredArgsConstructor
public class BusStationMultilingualController {

    private final BusStationMultilingualService busStationMultilingualService;
    
    /**
     * 다국어 버스 정류소 정보 수동 갱신
     */
    @GetMapping("/stations/multilingual/refresh")
    public ResponseEntity<Map<String, String>> refreshBusStationsMultilingual() {
        log.info("다국어 버스 정류소 정보 수동 갱신 요청");
        busStationMultilingualService.fetchAndSaveBusStationsMultilingual();
        return ResponseEntity.ok(Map.of("status", "success", "message", "다국어 버스 정류소 정보가 갱신되었습니다."));
    }
    
    /**
     * 최근 다국어 버스 정류소 정보 조회
     */
    @GetMapping("/stations/multilingual")
    public ResponseEntity<List<BusStationMultilingual>> getLatestBusStationsMultilingual(
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        log.info("최근 다국어 버스 정류소 정보 조회 요청: limit={}", limit);
        List<BusStationMultilingual> busStations = busStationMultilingualService.getLatestBusStationsMultilingual(limit);
        return ResponseEntity.ok(busStations);
    }
    
    /**
     * 특정 정류소 ID로 다국어 버스 정류소 정보 조회
     */
    @GetMapping("/stations/multilingual/{stationId}")
    public ResponseEntity<BusStationMultilingual> getBusStationMultilingualByStationId(@PathVariable String stationId) {
        log.info("특정 정류소 ID로 다국어 버스 정류소 정보 조회 요청: stationId={}", stationId);
        BusStationMultilingual busStation = busStationMultilingualService.getBusStationMultilingualByStationId(stationId);
        if (busStation == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(busStation);
    }
    
    /**
     * 키워드로 다국어 버스 정류소 정보 검색
     */
    @GetMapping("/stations/multilingual/search")
    public ResponseEntity<List<BusStationMultilingual>> searchBusStationsMultilingualByKeyword(
            @RequestParam String keyword,
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        log.info("키워드로 다국어 버스 정류소 정보 검색 요청: keyword={}, limit={}", keyword, limit);
        List<BusStationMultilingual> busStations = busStationMultilingualService.searchBusStationsMultilingualByKeyword(keyword, limit);
        return ResponseEntity.ok(busStations);
    }
    
    /**
     * 사용 중인 다국어 버스 정류소 정보 조회
     */
    @GetMapping("/stations/multilingual/active")
    public ResponseEntity<List<BusStationMultilingual>> getActiveBusStationsMultilingual() {
        log.info("사용 중인 다국어 버스 정류소 정보 조회 요청");
        List<BusStationMultilingual> busStations = busStationMultilingualService.getActiveBusStationsMultilingual();
        return ResponseEntity.ok(busStations);
    }
}
