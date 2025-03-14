package org.alljeju.alljejuserver.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BusVehicle {
    private Long id;
    private String vhId;          // 차량 ID
    private String plateNo;       // 차량 번호판
    private String lowPlateTp;    // 저상 버스 여부
    private String useYn;         // 사용 여부
    private LocalDateTime upd;    // 업데이트 시간
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
