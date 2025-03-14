package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusStationMultilingual;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusStationMultilingualMapper {
    
    @Insert({
        "<script>",
        "INSERT INTO bus_station_multilingual (stationId, stationNm, stationNmEn, stationNmCh, stationNmJp, govNm, localX, localY, dirTp, mobiNum, useYn, upd, created_at, updated_at)",
        "VALUES",
        "<foreach collection='busStations' item='item' separator=','>",
        "  (#{item.stationId}, #{item.stationNm}, #{item.stationNmEn}, #{item.stationNmCh}, #{item.stationNmJp}, #{item.govNm}, #{item.localX}, #{item.localY}, #{item.dirTp}, #{item.mobiNum}, #{item.useYn}, #{item.upd}, NOW(), NOW())",
        "</foreach>",
        "ON DUPLICATE KEY UPDATE",
        "  stationNm = VALUES(stationNm),",
        "  stationNmEn = VALUES(stationNmEn),",
        "  stationNmCh = VALUES(stationNmCh),",
        "  stationNmJp = VALUES(stationNmJp),",
        "  govNm = VALUES(govNm),",
        "  localX = VALUES(localX),",
        "  localY = VALUES(localY),",
        "  dirTp = VALUES(dirTp),",
        "  mobiNum = VALUES(mobiNum),",
        "  useYn = VALUES(useYn),",
        "  upd = VALUES(upd),",
        "  updated_at = NOW()",
        "</script>"
    })
    int batchInsert(@Param("busStations") List<BusStationMultilingual> busStations);
    
    @Select("SELECT * FROM bus_station_multilingual ORDER BY id DESC LIMIT #{limit}")
    List<BusStationMultilingual> findLatest(@Param("limit") int limit);
    
    @Select("SELECT * FROM bus_station_multilingual WHERE stationId = #{stationId}")
    BusStationMultilingual findByStationId(@Param("stationId") String stationId);
    
    @Select("SELECT * FROM bus_station_multilingual WHERE stationNm LIKE CONCAT('%', #{keyword}, '%') OR stationNmEn LIKE CONCAT('%', #{keyword}, '%') OR stationNmCh LIKE CONCAT('%', #{keyword}, '%') OR stationNmJp LIKE CONCAT('%', #{keyword}, '%') ORDER BY stationNm LIMIT #{limit}")
    List<BusStationMultilingual> searchByKeyword(@Param("keyword") String keyword, @Param("limit") int limit);
    
    @Select("SELECT * FROM bus_station_multilingual WHERE useYn = 'Y' ORDER BY stationNm")
    List<BusStationMultilingual> findActiveStations();
}
