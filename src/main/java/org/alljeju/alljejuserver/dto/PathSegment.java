package org.alljeju.alljejuserver.dto;

import lombok.Data;
import org.alljeju.alljejuserver.dto.graph.RouteEdge;

import java.util.ArrayList;
import java.util.List;

/**
 * 버스 경로의 세그먼트(구간)를 나타내는 클래스
 * 하나의 세그먼트는 동일한 교통수단(버스 노선)으로 이동하는 구간을 의미
 */
@Data
public class PathSegment {
    private String type;                   // 세그먼트 유형 (BUS, WALK 등)
    private String routeId;                // 버스 노선 ID
    private String routeName;              // 버스 노선 이름
    private List<String> stationIds;       // 경유 정류소 ID 목록
    private List<String> stationNames;     // 경유 정류소 이름 목록
    private int distanceMeters;            // 구간 거리 (미터)
    private int minutes;                   // 구간 소요시간 (분)
    private int stationCount;              // 정류장 수
    private List<RouteEdge> edges;         // 구간의 엣지 목록
    private List<String> viaStationIds;    // 경유 정류장 ID 목록 (출발-도착 포함)
    private List<String> viaStationNames;  // 경유 정류장 이름 목록 (출발-도착 포함)

    public PathSegment() {
        this.stationIds = new ArrayList<>();
        this.stationNames = new ArrayList<>();
        this.edges = new ArrayList<>();
        this.viaStationIds = new ArrayList<>();
        this.viaStationNames = new ArrayList<>();
    }

    /**
     * 세그먼트에 엣지 추가
     * @param edge 추가할 엣지
     */
    public void addEdge(RouteEdge edge) {
        edges.add(edge);
        
        // 첫 엣지인 경우, 출발 정류소 정보도 추가
        if (edges.size() == 1) {
            stationIds.add(edge.getSource().getStationId());
            stationNames.add(edge.getSource().getStationName());
        }
        
        // 도착 정류소 정보 추가
        stationIds.add(edge.getTarget().getStationId());
        stationNames.add(edge.getTarget().getStationName());
        
        // 거리 및 시간 누적
        distanceMeters += edge.getDistance();
        minutes += edge.getTime();
    }

    public void setStationCount(int stationCount) {
        this.stationCount = stationCount;
    }

    public void setViaStationIds(List<String> viaStationIds) {
        this.viaStationIds = viaStationIds;
    }

    public void setViaStationNames(List<String> viaStationNames) {
        this.viaStationNames = viaStationNames;
    }
}
