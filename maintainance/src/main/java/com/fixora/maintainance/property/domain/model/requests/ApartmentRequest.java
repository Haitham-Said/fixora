package com.fixora.maintainance.property.domain.model.requests;

import lombok.Data;

@Data
public class ApartmentRequest {
    private Long buildingId;
    private String apartmentNumber;
    private Integer floorNumber;
}

