package org.alljeju.alljejuserver.mapper;

import org.alljeju.alljejuserver.model.RealEstate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface RealEstateMapper {
    void insertRealEstate(RealEstate realEstate);

    RealEstate selectRealEstateAptSeq(String aptSeq);

    void updateRealEstate(RealEstate realEstate);

    RealEstate findByChecksum(String checksum);
    void insertOrUpdate(RealEstate realEstate);

    List<RealEstate> selectAllRealEstates(Map<String, Object> params);
    int selectAllRealEstatesCount(Map<String, Object> params);

    RealEstate selectRealEstateById(Map<String, Object> params);

    List<RealEstate> selectTopMonthlyTransactions(String dealDate);
}
