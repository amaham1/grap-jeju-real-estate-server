package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusStation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusStationMapper {
    
    @Insert("INSERT INTO bus_station (dirTp, govNm, localX, localY, mobiNum, stationId, stationNm, upd, useYn) " +
            "VALUES (#{dirTp}, #{govNm}, #{localX}, #{localY}, #{mobiNum}, #{stationId}, #{stationNm}, #{upd}, #{useYn})")
    void insert(BusStation busStation);
    
    @Insert("<script>" +
            "INSERT INTO bus_station (dirTp, govNm, localX, localY, mobiNum, stationId, stationNm, upd, useYn) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.dirTp}, #{item.govNm}, #{item.localX}, #{item.localY}, #{item.mobiNum}, #{item.stationId}, #{item.stationNm}, #{item.upd}, #{item.useYn})" +
            "</foreach>" +
            "</script>")
    void batchInsert(List<BusStation> busStations);
    
    @Select("SELECT * FROM bus_station ORDER BY id DESC LIMIT #{limit}")
    List<BusStation> findLatest(int limit);
    
    @Select("SELECT * FROM bus_station WHERE stationId = #{stationId}")
    BusStation findByStationId(String stationId);
    
    @Select("SELECT * FROM bus_station WHERE stationNm LIKE CONCAT('%', #{keyword}, '%') LIMIT #{limit}")
    List<BusStation> findByStationName(String keyword, int limit);
    
    @Select("SELECT * FROM bus_station")
    List<BusStation> findAllStations();
}
