package org.alljeju.alljejuserver.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BusStation {
    private Long id;
    private String dirTp;           // 방향 타입
    private String govNm;           // 관할 번호
    private BigDecimal localX;      // 정류소 경도
    private BigDecimal localY;      // 정류소 위도
    private String mobiNum;         // 모바일 정류소 번호
    private String stationId;       // 정류소 ID
    private String stationNm;       // 정류소 이름
    private LocalDateTime upd;      // 업데이트 시간
    private String useYn;           // 사용 여부
    private LocalDateTime createdAt; // 생성 시간
    private LocalDateTime updatedAt; // 수정 시간
}
