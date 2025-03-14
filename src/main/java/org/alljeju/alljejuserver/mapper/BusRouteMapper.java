package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusRoute;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusRouteMapper {
    
    @Insert({
        "<script>",
        "INSERT INTO bus_route (routeId, routeNm, routeNum, routeSubNm, routeTp, routeColor, routeLen, stationCnt, orgtStationId, dstStationId, govNm, useYn, upd, created_at, updated_at)",
        "VALUES",
        "<foreach collection='busRoutes' item='item' separator=','>",
        "  (#{item.routeId}, #{item.routeNm}, #{item.routeNum}, #{item.routeSubNm}, #{item.routeTp}, #{item.routeColor}, #{item.routeLen}, #{item.stationCnt}, #{item.orgtStationId}, #{item.dstStationId}, #{item.govNm}, #{item.useYn}, #{item.upd}, NOW(), NOW())",
        "</foreach>",
        "ON DUPLICATE KEY UPDATE",
        "  routeNm = VALUES(routeNm),",
        "  routeNum = VALUES(routeNum),",
        "  routeSubNm = VALUES(routeSubNm),",
        "  routeTp = VALUES(routeTp),",
        "  routeColor = VALUES(routeColor),",
        "  routeLen = VALUES(routeLen),",
        "  stationCnt = VALUES(stationCnt),",
        "  orgtStationId = VALUES(orgtStationId),",
        "  dstStationId = VALUES(dstStationId),",
        "  govNm = VALUES(govNm),",
        "  useYn = VALUES(useYn),",
        "  upd = VALUES(upd),",
        "  updated_at = NOW()",
        "</script>"
    })
    int batchInsert(@Param("busRoutes") List<BusRoute> busRoutes);
    
    @Select("SELECT * FROM bus_route ORDER BY id DESC LIMIT #{limit}")
    List<BusRoute> findLatest(@Param("limit") int limit);
    
    @Select("SELECT * FROM bus_route WHERE routeId = #{routeId}")
    BusRoute findByRouteId(@Param("routeId") String routeId);
    
    @Select("SELECT * FROM bus_route WHERE routeNm LIKE CONCAT('%', #{keyword}, '%') OR routeSubNm LIKE CONCAT('%', #{keyword}, '%') ORDER BY routeNm LIMIT #{limit}")
    List<BusRoute> searchByKeyword(@Param("keyword") String keyword, @Param("limit") int limit);
    
    @Select("SELECT * FROM bus_route ORDER BY routeNm")
    List<BusRoute> findActiveRoutes();
    
    @Select("SELECT * FROM bus_route WHERE routeNum = #{routeNum} ORDER BY routeNm")
    List<BusRoute> findByRouteNum(@Param("routeNum") String routeNum);
    
    @Select("SELECT * FROM bus_route")
    List<BusRoute> findAllRoutes();
}
