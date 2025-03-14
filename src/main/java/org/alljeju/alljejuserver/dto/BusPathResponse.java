package org.alljeju.alljejuserver.dto;

import lombok.Data;
import java.util.List;

/**
 * 버스 경로 검색 결과 응답 클래스
 */
@Data
public class BusPathResponse {
    private boolean reachable;               // 도달 가능 여부
    private String startStationId;           // 출발 정류소 ID
    private String destinationStationId;     // 도착 정류소 ID
    private boolean needTransfer;            // 환승 필요 여부
    private int transferCount;               // 환승 횟수
    private int usedRoutes;                  // 사용된 노선 수
    private String reason;                   // 결과 이유 설명
    private List<PathNode> path;             // 경로 정보
    private int pathStationCount;            // 경로 정류소 수
}
