package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.BusVehicle;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BusVehicleMapper {
    
    @Insert({
        "<script>",
        "INSERT INTO bus_vehicle (vhId, plateNo, lowPlateTp, useYn, upd, created_at, updated_at)",
        "VALUES",
        "<foreach collection='busVehicles' item='item' separator=','>",
        "  (#{item.vhId}, #{item.plateNo}, #{item.lowPlateTp}, #{item.useYn}, #{item.upd}, NOW(), NOW())",
        "</foreach>",
        "ON DUPLICATE KEY UPDATE",
        "  plateNo = VALUES(plateNo),",
        "  lowPlateTp = VALUES(lowPlateTp),",
        "  useYn = VALUES(useYn),",
        "  upd = VALUES(upd),",
        "  updated_at = NOW()",
        "</script>"
    })
    int batchInsert(@Param("busVehicles") List<BusVehicle> busVehicles);
    
    @Select("SELECT * FROM bus_vehicle ORDER BY id DESC LIMIT #{limit}")
    List<BusVehicle> findLatest(@Param("limit") int limit);
    
    @Select("SELECT * FROM bus_vehicle WHERE vhId = #{vhId}")
    BusVehicle findByVehicleId(@Param("vhId") String vhId);
    
    @Select("SELECT * FROM bus_vehicle WHERE plateNo LIKE CONCAT('%', #{keyword}, '%') ORDER BY plateNo LIMIT #{limit}")
    List<BusVehicle> findByPlateNo(@Param("keyword") String keyword, @Param("limit") int limit);
    
    @Select("SELECT * FROM bus_vehicle WHERE useYn = 'Y' ORDER BY plateNo")
    List<BusVehicle> findActiveVehicles();
}
