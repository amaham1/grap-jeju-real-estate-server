package org.alljeju.alljejuserver.model;

import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@JacksonXmlRootElement(localName = "response")
public class RealEstate {
    private int jsApRSId, buildYear, dealDay, startDealMonth, startDealYear, endDealMonth, endDealYear, floor, dealYear, dealMonth, dealAmount;
    private String aptDong, aptNm, aptSeq, bonbun, bubun,
                   buyerGbn, dealingGbn, jibun, landCd, landLeaseholdGbn, roadNmSeq, roadNmbCd,
                   roadNm, roadNmSggCd, roadNmBonbun, roadNmBubun, roadNmCd, sggCd, slerGbn, umdCd, umdNm, cdealType, cdealDay,
                   estateAgentSggNm, rgsTdate, longitude, latitude, useYn, deleteYn, createDate, lastUpdateDate;
    private double excluUseAr, price;
    private String checksum;  // 체크섬 필드

    // 체크섬 생성 메소드 수정
    public void generateChecksum() {
        String dataString = String.format("%s|%s|%s|%s|%s|%s|%f|%d|%s|%s",
            this.umdNm, this.jibun, this.aptSeq, this.aptNm, this.dealAmount, this.dealingGbn, this.excluUseAr, this.startDealYear, this.cdealType, this.cdealDay);
        this.checksum = DigestUtils.md5DigestAsHex(dataString.getBytes());
    }
}
