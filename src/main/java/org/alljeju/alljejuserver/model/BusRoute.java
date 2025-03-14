package org.alljeju.alljejuserver.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusRoute {
    private Long id;
    private String routeId;       // 노선 ID
    private String routeNm;       // 노선 이름
    private String routeNum;      // 노선 번호
    private String routeSubNm;    // 노선 서브 이름
    private String routeTp;       // 노선 유형
    private String routeColor;    // 노선 색상
    private String routeTpNm;     // 노선 유형 이름
    private String routeType;   // 노선 유형 설명
    private Integer routeLen;     // 노선 길이
    private Integer stationCnt;   // 정류소 수
    private String orgtStationId; // 출발 정류소 ID
    private String dstStationId;  // 도착 정류소 ID
    private String govNm;         // 관할지역 코드
    private String useYn;         // 사용 여부
    private LocalDateTime upd;    // 업데이트 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
