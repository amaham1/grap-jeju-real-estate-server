package org.alljeju.alljejuserver.dto.graph;

import lombok.Data;

/**
 * 두 정류장 사이의 버스 노선 엣지를 나타내는 클래스
 */
@Data
public class RouteEdge {
    private String routeId;           // 노선 ID
    private String routeName;         // 노선 이름
    private String routeNumber;       // 노선 번호
    private StationNode source;       // 출발 정류장
    private StationNode destination;  // 도착 정류장
    private Integer distance;         // 거리 (미터)
    private Integer travelTime;       // 소요 시간 (초)
    private Integer stationOrd;       // 정류장 순서
    
    /**
     * 노선 엣지 생성자
     * 
     * @param routeId 노선 ID
     * @param routeName 노선 이름
     * @param routeNumber 노선 번호
     * @param source 출발 정류장
     * @param destination 도착 정류장
     * @param distance 거리 (미터)
     * @param stationOrd 정류장 순서
     */
    public RouteEdge(String routeId, String routeName, String routeNumber, 
                    StationNode source, StationNode destination, 
                    Integer distance, Integer stationOrd) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.routeNumber = routeNumber;
        this.source = source;
        this.destination = destination;
        this.distance = distance;
        this.stationOrd = stationOrd;
        
        // 거리에 따른 소요 시간 추정 (평균 시속 25km로 가정)
        // 거리(m) / 속도(m/s) = 시간(s)
        // 25km/h = 약 6.94m/s
        if (distance != null) {
            this.travelTime = (int) (distance / 6.94);
        }
    }
    
    /**
     * 도착 정류장 노드 조회 (대체 메서드)
     * @return 도착 정류장 노드
     */
    public StationNode getTarget() {
        return destination;
    }
    
    /**
     * 소요 시간 조회 (분 단위, 대체 메서드)
     * @return 소요 시간 (분)
     */
    public Integer getTime() {
        if (travelTime == null) {
            return 0;
        }
        // 초 단위에서 분 단위로 변환하여 반환 (올림)
        return (int) Math.ceil(travelTime / 60.0);
    }
    
    /**
     * 소요 시간 조회 (초 단위)
     * @return 소요 시간 (초)
     */
    public Integer getTravelTime() {
        return travelTime != null ? travelTime : 0;
    }
}
