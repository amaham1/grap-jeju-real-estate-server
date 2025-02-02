package org.alljeju.alljejuserver.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RealEstateResponse {
    private List<RealEstate> realEstates;
    private int totalCount;
}
