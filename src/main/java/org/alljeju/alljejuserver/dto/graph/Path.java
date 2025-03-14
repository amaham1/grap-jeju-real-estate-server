package org.alljeju.alljejuserver.dto.graph;

import lombok.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 경로를 나타내는 클래스 - 다익스트라 알고리즘에서 사용
 */
@Data
public class Path implements Comparable<Path> {
    private List<RouteEdge> edges;            // 경로를 구성하는 엣지들
    private Set<String> usedRouteIds;         // 사용된 노선 ID (환승 횟수 계산용)
    private Integer totalDistance;            // 총 거리 (미터)
    private Integer totalTime;                // 총 소요 시간 (초)
    private Integer transferCount;            // 환승 횟수
    private StationNode startStation;         // 시작 정류장
    private StationNode currentStation;       // 현재 정류장
    
    /**
     * 기본 생성자 (시작 정류장 없음)
     */
    public Path() {
        this.edges = new ArrayList<>();
        this.usedRouteIds = new HashSet<>();
        this.totalDistance = 0;
        this.totalTime = 0;
        this.transferCount = 0;
    }
    
    /**
     * 새 경로 생성
     * 
     * @param startStation 시작 정류장
     */
    public Path(StationNode startStation) {
        this.edges = new ArrayList<>();
        this.usedRouteIds = new HashSet<>();
        this.totalDistance = 0;
        this.totalTime = 0;
        this.transferCount = 0;
        this.startStation = startStation;
        this.currentStation = startStation;
    }
    
    /**
     * 기존 경로를 복사하여 새 경로 생성
     * 
     * @param path 복사할 경로
     */
    public Path(Path path) {
        this.edges = new ArrayList<>(path.edges);
        this.usedRouteIds = new HashSet<>(path.usedRouteIds);
        this.totalDistance = path.totalDistance;
        this.totalTime = path.totalTime;
        this.transferCount = path.transferCount;
        this.startStation = path.startStation;
        this.currentStation = path.currentStation;
    }
    
    /**
     * 경로에 엣지 추가
     * 
     * @param edge 추가할 엣지
     */
    public void addEdge(RouteEdge edge) {
        edges.add(edge);
        
        // 거리와 시간 누적
        if (edge.getDistance() != null) {
            totalDistance += edge.getDistance();
        }
        
        if (edge.getTravelTime() != null) {
            totalTime += edge.getTravelTime();
        }
        
        // 환승 횟수 계산 - 이전에 사용하지 않은 노선이면 환승 횟수 증가
        if (!usedRouteIds.isEmpty() && !usedRouteIds.contains(edge.getRouteId())) {
            transferCount++;
        }
        
        // 사용된 노선 ID 추가
        usedRouteIds.add(edge.getRouteId());
        
        // 현재 정류장 업데이트
        currentStation = edge.getDestination();
    }
    
    /**
     * 경로에 엣지 추가 (대기 시간 포함)
     * 
     * @param edge 추가할 엣지
     * @param waitTimeMinutes 추가 대기 시간 (분)
     */
    public void addEdge(RouteEdge edge, int waitTimeMinutes) {
        edges.add(edge);
        
        // 거리와 시간 누적
        if (edge.getDistance() != null) {
            totalDistance += edge.getDistance();
        }
        
        if (edge.getTravelTime() != null) {
            totalTime += edge.getTravelTime();
        }
        
        // 대기 시간 추가 (분)
        if (waitTimeMinutes > 0) {
            totalTime += waitTimeMinutes * 60; // 분 -> 초 변환
        }
        
        // 환승 횟수 계산 - 이전에 사용하지 않은 노선이면 환승 횟수 증가
        if (!usedRouteIds.isEmpty() && !usedRouteIds.contains(edge.getRouteId())) {
            transferCount++;
        }
        
        // 사용한 노선 ID 추가
        usedRouteIds.add(edge.getRouteId());
        
        // 현재 정류장 업데이트
        currentStation = edge.getDestination();
    }
    
    /**
     * 도보 이동 시간 (분) 추가
     * 
     * @param walkTimeMinutes 도보 이동 시간 (분)
     */
    public void addWalkTime(int walkTimeMinutes) {
        totalTime += walkTimeMinutes * 60;  // 분을 초로 변환
    }
    
    /**
     * 대기 시간 (분) 추가
     * 
     * @param waitTimeMinutes 대기 시간 (분)
     */
    public void addWaitTime(int waitTimeMinutes) {
        totalTime += waitTimeMinutes * 60;  // 분을 초로 변환
    }
    
    /**
     * 환승 시간 추가 (기본 5분)
     */
    public void addTransferTime() {
        totalTime += 5 * 60;  // 5분을 초로 변환
    }
    
    /**
     * 경로에 정류장 추가 (첫 정류장으로)
     * 
     * @param station 추가할 정류장
     */
    public void addStation(StationNode station) {
        if (this.startStation == null) {
            this.startStation = station;
        }
        this.currentStation = station;
    }
    
    /**
     * 경로에 특정 정류장이 포함되어 있는지 확인
     * 
     * @param stationId 확인할 정류장 ID
     * @return 포함 여부
     */
    public boolean containsStation(String stationId) {
        if (startStation != null && startStation.getStationId().equals(stationId)) {
            return true;
        }
        
        for (RouteEdge edge : edges) {
            if (edge.getSource().getStationId().equals(stationId) || 
                edge.getDestination().getStationId().equals(stationId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 마지막 정류장 가져오기
     * 
     * @return 경로의 마지막 정류장
     */
    public StationNode getLastStation() {
        if (edges.isEmpty()) {
            return startStation;
        }
        return edges.get(edges.size() - 1).getDestination();
    }
    
    /**
     * 마지막 엣지 가져오기
     * 
     * @return 경로의 마지막 엣지 (없으면 null)
     */
    public RouteEdge getLastEdge() {
        if (edges.isEmpty()) {
            return null;
        }
        return edges.get(edges.size() - 1);
    }
    
    /**
     * 경로의 정류장 ID 시퀀스 문자열 가져오기
     * 
     * @return 정류장 ID 시퀀스 (쉼표로 구분)
     */
    public String getStationIdSequence() {
        StringBuilder sb = new StringBuilder();
        
        if (startStation != null) {
            sb.append(startStation.getStationId());
        }
        
        for (RouteEdge edge : edges) {
            sb.append(" → ");
            sb.append(edge.getDestination().getStationId());
        }
        
        return sb.toString();
    }
    
    /**
     * 경로 비교 (소요 시간 기준)
     */
    @Override
    public int compareTo(Path other) {
        return Integer.compare(this.totalTime, other.totalTime);
    }
    
    /**
     * 총 소요 시간 (분) 가져오기
     * 
     * @return 총 소요 시간 (분)
     */
    public int getTotalTimeMinutes() {
        return totalTime / 60;
    }
}
