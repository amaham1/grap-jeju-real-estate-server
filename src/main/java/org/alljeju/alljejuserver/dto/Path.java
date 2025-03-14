package org.alljeju.alljejuserver.dto;

import lombok.Data;
import org.alljeju.alljejuserver.dto.graph.RouteEdge;
import org.alljeju.alljejuserver.dto.graph.StationNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 버스 경로를 나타내는 클래스
 */
@Data
public class Path {
    private String startStationId;            // 출발 정류소 ID
    private String destinationStationId;      // 도착 정류소 ID
    private List<RouteEdge> edges;            // 경로의 엣지(간선) 목록
    private int transferCount;                // 환승 횟수
    private int totalDistance;                // 총 거리 (미터)
    private int totalTime;                    // 총 소요시간 (분)
    private int totalTravelTimeMinutes;       // 총 이동 소요시간 (분)
    private int totalTimeMinutes;       // 총 이동 소요시간 (분)
    private int totalWaitTimeMinutes;         // 총 대기 소요시간 (분)
    
    private List<PathSegment> segments;       // 경로 세그먼트 목록
    private Set<String> usedRouteIds;         // 사용된 노선 ID 목록
    private StationNode currentStation;       // 현재 위치한 정류소
    private List<String> stationIdSequence;   // 정류장 ID 순서 목록

    public Path() {
        this.edges = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.usedRouteIds = new HashSet<>();
        this.stationIdSequence = new ArrayList<>();
    }
    
    /**
     * 기존 경로를 복사하여 새 경로 생성 (깊은 복사)
     * @param original 원본 경로
     */
    public Path(Path original) {
        this.startStationId = original.startStationId;
        this.destinationStationId = original.destinationStationId;
        this.edges = new ArrayList<>(original.edges);
        this.transferCount = original.transferCount;
        this.totalDistance = original.totalDistance;
        this.totalTime = original.totalTime;
        this.segments = new ArrayList<>(original.segments);
        this.usedRouteIds = new HashSet<>(original.usedRouteIds);
        this.currentStation = original.currentStation;
        this.stationIdSequence = new ArrayList<>(original.stationIdSequence);
    }

    /**
     * 정류장 노드로 경로 생성
     * @param station 시작 정류장 노드
     */
    public Path(StationNode station) {
        this.edges = new ArrayList<>();
        this.segments = new ArrayList<>();
        this.usedRouteIds = new HashSet<>();
        this.stationIdSequence = new ArrayList<>();
        this.transferCount = 0;
        this.totalDistance = 0;
        this.totalTime = 0;
        this.currentStation = station;
        if (station != null) {
            this.startStationId = station.getStationId();
            this.stationIdSequence.add(station.getStationId());
        }
    }

    /**
     * 경로에 엣지 추가
     * @param edge 추가할 엣지
     */
    public void addEdge(RouteEdge edge) {
        edges.add(edge);
        usedRouteIds.add(edge.getRouteId());
        currentStation = edge.getTarget(); // 현재 위치 업데이트
        
        // 정류장 ID 순서 업데이트
        if (edges.size() == 1) {
            // 첫 번째 엣지인 경우 출발 정류장도 추가
            stationIdSequence.add(edge.getSource().getStationId());
        }
        stationIdSequence.add(edge.getTarget().getStationId());
        
        // 거리와 시간 업데이트
        totalDistance += edge.getDistance();
        totalTime += edge.getTime();
    }

    /**
     * 경로 세그먼트 추가
     * @param segment 추가할 세그먼트
     */
    public void addSegment(PathSegment segment) {
        segments.add(segment);
    }

    /**
     * 경로 시작 정류소 ID 조회
     * @return 시작 정류소 ID
     */
    public String getStartStationId() {
        if (edges.isEmpty()) {
            return startStationId;
        }
        return edges.get(0).getSource().getStationId();
    }

    /**
     * 경로 도착 정류소 ID 조회
     * @return 도착 정류소 ID
     */
    public String getDestinationStationId() {
        if (edges.isEmpty()) {
            return destinationStationId;
        }
        return edges.get(edges.size() - 1).getTarget().getStationId();
    }
    
    /**
     * 현재 경로에서 사용중인 노선 ID 목록 반환
     * @return 노선 ID Set
     */
    public Set<String> getUsedRouteIds() {
        return usedRouteIds;
    }
    
    /**
     * 현재 위치한 정류소 반환
     * @return 현재 정류소 노드
     */
    public StationNode getCurrentStation() {
        return currentStation;
    }
    
    /**
     * 현재 위치한 정류소 설정
     * @param station 현재 정류소 노드
     */
    public void setCurrentStation(StationNode station) {
        this.currentStation = station;
    }
    
    /**
     * 정류장 ID 순서 목록 조회
     * @return 정류장 ID 순서 목록
     */
    public List<String> getStationIdSequence() {
        return stationIdSequence;
    }
    
    /**
     * 환승 시간 추가 (기본 환승 시간: 5분)
     */
    public void addTransferTime() {
        transferCount++;
        totalTime += 5; // 환승 시간 5분 추가
    }
    
    /**
     * 버스 대기 시간 추가
     * @param waitTimeMinutes 대기 시간 (분)
     */
    public void addWaitTime(int waitTimeMinutes) {
        totalTime += waitTimeMinutes;
    }
}
