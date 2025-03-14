package org.alljeju.alljejuserver.dto;

import lombok.Data;
import java.util.List;

@Data
public class PathFindingResponse {
    private boolean success;
    private String message;
    private List<BusRoutePath> paths;  // 여러 경로 옵션 제공
    private Integer totalTravelTimeMinutes; // 총 소요 시간 (분)
    private Integer totalDistanceMeters; // 총 이동 거리 (미터)
    private Integer transferCount;    // 환승 횟수
    
    @Data
    public static class BusRoutePath {
        private List<PathSegment> segments; // 경로 세그먼트 목록
        private Integer totalMinutes;       // 총 소요 시간 (분)
        private Integer totalDistanceMeters; // 총 이동 거리 (미터)
        private Integer transferCount;      // 환승 횟수
    }
    
    @Data
    public static class PathSegment {
        private String type;          // WALK(도보) 또는 BUS(버스)
        private String routeId;       // 버스 노선 ID (버스 세그먼트인 경우)
        private String routeName;     // 버스 노선 이름 (버스 세그먼트인 경우)
        private String routeNumber;   // 버스 노선 번호 (버스 세그먼트인 경우)
        private String startStationId;   // 출발 정류장 ID
        private String startStationName; // 출발 정류장 이름
        private String endStationId;     // 도착 정류장 ID
        private String endStationName;   // 도착 정류장 이름
        private List<String> viaStationIds;  // 경유 정류장 ID 목록
        private List<String> viaStationNames; // 경유 정류장 이름 목록
        private Integer minutes;        // 소요 시간 (분)
        private Integer distanceMeters; // 이동 거리 (미터)
        private Integer stationCount;   // 정류장 수
        private Integer predictTravTm;  // 예상 도착 시간 (분)
    }
}
