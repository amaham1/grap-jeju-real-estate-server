package org.alljeju.alljejuserver.dto;

import lombok.Data;

@Data
public class PathFindingRequest {
    private String startStationId;    // 출발 정류장 ID
    private String destinationStationId;  // 도착 정류장 ID
    private String searchType;   // 검색 유형 (최소시간, 최소환승, 최소거리)
    private Integer maxTransfers;  // 최대 환승 횟수 (선택)
}
