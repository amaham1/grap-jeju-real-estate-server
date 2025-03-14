package org.alljeju.alljejuserver.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusRouteStation {
    private Long id;
    private String routeId;       // 노선 ID
    private String routeNm;       // 노선 이름
    private String routeSubNm;    // 노선 서브 이름
    private String routeTp;       // 노선 유형
    private String stationId;     // 정류장 ID
    private String stationNm;     // 정류장 이름
    private Integer stationOrd;   // 정류장 순서
    private String updnDir;       // 상하행 구분 (1:상행, 2:하행)
    private Double localX;        // X 좌표
    private Double localY;        // Y 좌표
    private String lowPlateTp;    // 저상 버스 유형
    private Integer mobiNum;      // 모비넘버
    private String plateNo;       // 차량 번호
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
