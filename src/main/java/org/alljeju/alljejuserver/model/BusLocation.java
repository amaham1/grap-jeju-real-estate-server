package org.alljeju.alljejuserver.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BusLocation {
    private Long id;
    private BigDecimal localX;        // 버스 위치 경도
    private BigDecimal localY;        // 버스 위치 위도
    private String lowPlateTp;        // 저상버스
    private String mobiNum;           // 모바일 정류소 번호
    private String plateNo;           // 차량 번호판
    private String routeId;           // 노선ID
    private String routeNm;           // 노선번호
    private String routeSubNm;        // 목적지
    private String routeTp;           // 노선타입
    private String stationId;         // 정류소ID
    private String stationNm;         // 정류소명
    private Integer stationOrd;       // 정류소 순번
    private String updnDir;           // 상하행구분
    private LocalDateTime createdAt;  // 생성 시간
    private LocalDateTime updatedAt;  // 수정 시간
}
