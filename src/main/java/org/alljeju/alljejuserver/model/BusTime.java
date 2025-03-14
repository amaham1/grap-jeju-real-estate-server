package org.alljeju.alljejuserver.model;

import lombok.Data;

/**
 * 버스 운행 시간 모델 클래스
 */
@Data
public class BusTime {
    private String routeId;        // 버스 노선 ID
    private String dayType;        // 요일 유형 (평일, 토요일, 일요일/공휴일)
    private String startTime;      // 첫차 시간
    private String endTime;        // 막차 시간
    private Integer interval;      // 운행 간격 (분)
}
