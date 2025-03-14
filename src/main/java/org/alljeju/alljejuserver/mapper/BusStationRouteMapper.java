package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusStationRoute;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusStationRouteMapper {
    
    @Insert({
        "<script>",
        "INSERT INTO bus_station_route (routeId, stationId, stationOrd, routeDist, waypointOrd, updnDir, useYn, upd, created_at, updated_at)",
        "VALUES",
        "<foreach collection='busStationRoutes' item='item' separator=','>",
        "  (#{item.routeId}, #{item.stationId}, #{item.stationOrd}, #{item.routeDist}, #{item.waypointOrd}, #{item.updnDir}, #{item.useYn}, #{item.upd}, NOW(), NOW())",
        "</foreach>",
        "ON DUPLICATE KEY UPDATE",
        "  stationOrd = VALUES(stationOrd),",
        "  routeDist = VALUES(routeDist),",
        "  waypointOrd = VALUES(waypointOrd),",
        "  updnDir = VALUES(updnDir),",
        "  useYn = VALUES(useYn),",
        "  upd = VALUES(upd),",
        "  updated_at = NOW()",
        "</script>"
    })
    int batchInsert(@Param("busStationRoutes") List<BusStationRoute> busStationRoutes);
    
    @Select("SELECT * FROM bus_station_route ORDER BY id DESC LIMIT #{limit}")
    List<BusStationRoute> findLatest(@Param("limit") int limit);
    
    @Select("SELECT * FROM bus_station_route WHERE routeId = #{routeId} ORDER BY stationOrd")
    List<BusStationRoute> findByRouteId(@Param("routeId") String routeId);
    
    @Select("SELECT * FROM bus_station_route WHERE stationId = #{stationId} ORDER BY routeId, stationOrd")
    List<BusStationRoute> findByStationId(@Param("stationId") String stationId);
    
    @Select("SELECT * FROM bus_station_route WHERE useYn = 'Y'")
    List<BusStationRoute> findAllStationRoutes();
    
    @Select("SELECT DISTINCT routeId FROM bus_station_route WHERE stationId = #{stationId} AND useYn = 'Y'")
    List<String> findRouteIdsByStationId(@Param("stationId") String stationId);
    
    @Select("SELECT * FROM bus_station_route WHERE routeId = #{routeId} AND stationId = #{stationId}")
    BusStationRoute findByRouteIdAndStationId(@Param("routeId") String routeId, @Param("stationId") String stationId);
    
    @Select("SELECT * FROM bus_station_route WHERE stationId = #{stationId} AND useYn = #{useYn}")
    List<BusStationRoute> findByStationIdAndUseYn(@Param("stationId") String stationId, @Param("useYn") String useYn);
    
    @Select("SELECT * FROM bus_station_route WHERE routeId = #{routeId} AND updnDir = #{updnDir} AND useYn = #{useYn} ORDER BY stationOrd ASC")
    List<BusStationRoute> findByRouteIdAndUpdnDirAndUseYnOrderByStationOrdAsc(
            @Param("routeId") String routeId, 
            @Param("updnDir") String updnDir, 
            @Param("useYn") String useYn);
    
    @Select("SELECT * FROM bus_station_route WHERE useYn = #{useYn}")
    List<BusStationRoute> findByUseYn(@Param("useYn") String useYn);
}
