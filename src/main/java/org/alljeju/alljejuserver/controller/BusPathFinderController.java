package org.alljeju.alljejuserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.alljeju.alljejuserver.dto.PathFindingRequest;
import org.alljeju.alljejuserver.dto.PathFindingResponse;
import org.alljeju.alljejuserver.service.BusPathFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 버스 경로 찾기 API 컨트롤러
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bus/path")
public class BusPathFinderController {

    private final BusPathFinderService busPathFinderService;

    /**
     * 버스 경로 찾기 API
     * 출발 정류장과 도착 정류장 사이의 최적 경로를 찾습니다.
     *
     * @param request 경로 찾기 요청 (출발지, 목적지, 검색 유형)
     * @return 경로 찾기 결과
     */
    @PostMapping("/find")
    public ResponseEntity<PathFindingResponse> findPath(@RequestBody PathFindingRequest request) {
        log.info("버스 경로 찾기 요청: {}", request);
        
        // 요청 데이터 검증
        if (request.getStartStationId() == null || request.getDestinationStationId() == null) {
            PathFindingResponse errorResponse = new PathFindingResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("출발지와 목적지 정류장 ID는 필수 입력 항목입니다.");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        // 서비스에 경로 찾기 요청
        // PathFindingResponse response = busPathFinderService.findPath(request);
        PathFindingResponse response = new PathFindingResponse();
        
        // 응답 데이터 반환
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.ok(response);  // 경로를 찾지 못했더라도 정상 응답으로 처리
        }
    }
    
    /**
     * 특정 정류장에 도착 예정인 버스 정보 조회 API
     *
     * @param stationId 정류장 ID
     * @return 버스 도착 정보
     */
    // @GetMapping("/arrival/{stationId}")
    // public ResponseEntity<?> getBusArrivalInfo(@PathVariable String stationId) {
    //     log.info("버스 도착 정보 조회 요청: 정류장 ID={}", stationId);
        
    //     // 정류장 ID 검증
    //     if (stationId == null || stationId.trim().isEmpty()) {
    //         return ResponseEntity.badRequest().body("정류장 ID는 필수 입력 항목입니다.");
    //     }
        
    //     // 서비스에 도착 정보 조회 요청
    //     try {   
    //         return ResponseEntity.ok(busPathFinderService.getBusArrivalInfo(stationId));
    //     } catch (Exception e) {
    //         log.error("버스 도착 정보 조회 중 오류 발생", e);
    //         return ResponseEntity.internalServerError().body("버스 도착 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
    //     }
    // }

    /**
     * 출발 정류장과 도착 정류장 사이의 도달 가능성 확인 API
     *
     * @param request 경로 찾기 요청 (출발지, 목적지)
     * @return 도달 가능성 확인 결과
     */
    @PostMapping("/isReachable")
    public ResponseEntity<?> checkReachable(@RequestBody PathFindingRequest request) {
        
        Map<String, Object> result = busPathFinderService.isReachable(request.getStartStationId(), request.getDestinationStationId());
        
        return ResponseEntity.ok(result);
    }
}
