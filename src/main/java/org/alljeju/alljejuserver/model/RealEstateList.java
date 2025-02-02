package org.alljeju.alljejuserver.model;


import org.springframework.util.DigestUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Data
@JacksonXmlRootElement(localName = "response")
public class RealEstateList {
    private int totalCount;

    private List<RealEstate> realEstates;

    public RealEstateList(List<RealEstate> realEstates, int totalCount) {
        this.realEstates = realEstates;
        this.totalCount = totalCount;
    }

}
