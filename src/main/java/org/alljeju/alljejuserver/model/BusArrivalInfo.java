package org.alljeju.alljejuserver.model;

import lombok.Data;

/**
 * 버스 도착 정보 모델 클래스
 */
@Data
public class BusArrivalInfo {
    private String routeId;        // 버스 노선 ID
    private String routeName;      // 버스 노선 이름
    private String stationId;      // 정류소 ID
    private String stationName;    // 정류소 이름
    private Integer arrTimeMinutes; // 도착 예정 시간(분)
    private String vehicleNo;      // 차량 번호
    private Integer remainSeatCnt;  // 남은 좌석 수
    private String updateTime;     // 정보 갱신 시간
}
