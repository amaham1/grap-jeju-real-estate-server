package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusLocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusLocationMapper {
    
    @Insert("INSERT INTO bus_location (localX, localY, lowPlateTp, mobiNum, plateNo, routeId, routeNm, routeSubNm, routeTp, stationId, stationNm, stationOrd, updnDir) " +
            "VALUES (#{localX}, #{localY}, #{lowPlateTp}, #{mobiNum}, #{plateNo}, #{routeId}, #{routeNm}, #{routeSubNm}, #{routeTp}, #{stationId}, #{stationNm}, #{stationOrd}, #{updnDir})")
    void insert(BusLocation busLocation);
    
    @Insert("<script>" +
            "INSERT INTO bus_location (localX, localY, lowPlateTp, mobiNum, plateNo, routeId, routeNm, routeSubNm, routeTp, stationId, stationNm, stationOrd, updnDir) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.localX}, #{item.localY}, #{item.lowPlateTp}, #{item.mobiNum}, #{item.plateNo}, #{item.routeId}, #{item.routeNm}, #{item.routeSubNm}, #{item.routeTp}, #{item.stationId}, #{item.stationNm}, #{item.stationOrd}, #{item.updnDir})" +
            "</foreach>" +
            "</script>")
    void batchInsert(List<BusLocation> busLocations);
    
    @Select("SELECT * FROM bus_location ORDER BY id DESC LIMIT #{limit}")
    List<BusLocation> findLatest(int limit);
    
    @Select("SELECT * FROM bus_location WHERE routeId = #{routeId} ORDER BY stationOrd")
    List<BusLocation> findByRouteId(String routeId);
}