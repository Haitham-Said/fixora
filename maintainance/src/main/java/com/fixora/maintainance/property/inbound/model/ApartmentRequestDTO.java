package com.fixora.maintainance.property.inbound.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApartmentRequestDTO {
    private Long apartmentId;
    private String apartmentNumber;
    private Integer floorNumber;
}

