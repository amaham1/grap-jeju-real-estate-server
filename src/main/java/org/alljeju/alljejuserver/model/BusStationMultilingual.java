package org.alljeju.alljejuserver.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusStationMultilingual {
    private Long id;
    private String stationId;     // 정류소 ID
    private String stationNm;     // 정류소 이름(한국어)
    private String stationNmEn;   // 정류소 이름(영어)
    private String stationNmCh;   // 정류소 이름(중국어)
    private String stationNmJp;   // 정류소 이름(일본어)
    private String govNm;         // 관할지역 코드
    private Double localX;        // X 좌표
    private Double localY;        // Y 좌표
    private String dirTp;         // 방향
    private Integer mobiNum;      // 모비넘버
    private String useYn;         // 사용 여부
    private LocalDateTime upd;    // 업데이트 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
