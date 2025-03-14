package org.alljeju.alljejuserver.dto;

import lombok.Data;

/**
 * 경로에서 정류소 노드를 나타내는 클래스
 */
@Data
public class PathNode {
    private String stationId;     // 정류소 ID
    private String stationName;   // 정류소 이름
    private boolean transfer;   // 환승 여부
    private String routeId;       // 버스 노선 ID
    private String routeName;     // 버스 노선 이름
}
