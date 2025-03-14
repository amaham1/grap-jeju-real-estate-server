package org.alljeju.alljejuserver.dto.graph;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 정류장 노드를 나타내는 클래스
 */
@Data
public class StationNode {
    private String stationId;       // 정류장 ID
    private String stationName;     // 정류장 이름
    private BigDecimal localX;      // 경도
    private BigDecimal localY;      // 위도
    private List<RouteEdge> edges;  // 이 정류장에서 출발하는 노선 엣지 목록
    
    public StationNode(String stationId, String stationName) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.edges = new ArrayList<>();
    }
    
    public StationNode(String stationId, String stationName, BigDecimal localX, BigDecimal localY) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.localX = localX;
        this.localY = localY;
        this.edges = new ArrayList<>();
    }
    
    public void addEdge(RouteEdge edge) {
        edges.add(edge);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationNode that = (StationNode) o;
        return stationId.equals(that.stationId);
    }
    
    @Override
    public int hashCode() {
        return stationId.hashCode();
    }
}
