package org.alljeju.alljejuserver.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusStationRoute {
    private Long id;
    private String routeId;       // 노선 ID
    private String stationId;     // 정류소 ID
    private Integer stationOrd;   // 정류소 순서
    private Integer routeDist;    // 노선 거리
    private Integer waypointOrd;  // 경유지 순서
    private String updnDir;       // 상하행 방향
    private String useYn;         // 사용 여부
    private LocalDateTime upd;    // 업데이트 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
