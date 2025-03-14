package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusRouteStation;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusRouteStationMapper {
    
    @Insert({
        "<script>",
        "INSERT INTO bus_route_station (routeId, routeNm, routeSubNm, routeTp, stationId, stationNm, stationOrd, updnDir, localX, localY, lowPlateTp, mobiNum, plateNo, created_at, updated_at)",
        "VALUES",
        "<foreach collection='busRouteStations' item='item' separator=','>",
        "  (#{item.routeId}, #{item.routeNm}, #{item.routeSubNm}, #{item.routeTp}, #{item.stationId}, #{item.stationNm}, #{item.stationOrd}, #{item.updnDir}, #{item.localX}, #{item.localY}, #{item.lowPlateTp}, #{item.mobiNum}, #{item.plateNo}, NOW(), NOW())",
        "</foreach>",
        "ON DUPLICATE KEY UPDATE",
        "  routeNm = VALUES(routeNm),",
        "  routeSubNm = VALUES(routeSubNm),",
        "  routeTp = VALUES(routeTp),",
        "  stationNm = VALUES(stationNm),",
        "  stationOrd = VALUES(stationOrd),",
        "  updnDir = VALUES(updnDir),",
        "  localX = VALUES(localX),",
        "  localY = VALUES(localY),",
        "  lowPlateTp = VALUES(lowPlateTp),",
        "  mobiNum = VALUES(mobiNum),",
        "  plateNo = VALUES(plateNo),",
        "  updated_at = NOW()",
        "</script>"
    })
    int batchInsert(@Param("busRouteStations") List<BusRouteStation> busRouteStations);
    
    @Select("SELECT * FROM bus_route_station WHERE routeId = #{routeId} ORDER BY stationOrd")
    List<BusRouteStation> findByRouteId(@Param("routeId") String routeId);
    
    @Select("SELECT * FROM bus_route_station WHERE stationId = #{stationId}")
    List<BusRouteStation> findByStationId(@Param("stationId") String stationId);
    
    @Select("SELECT DISTINCT routeId FROM bus_route WHERE useYn = 'Y'")
    List<String> findAllRouteIds();
    
    @Select("SELECT * FROM bus_route_station WHERE stationNm LIKE CONCAT('%', #{keyword}, '%') ORDER BY routeId, stationOrd LIMIT #{limit}")
    List<BusRouteStation> searchByKeyword(@Param("keyword") String keyword, @Param("limit") int limit);
}
