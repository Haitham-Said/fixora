package com.fixora.maintainance.property.domain.model.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildingRequest {
    private Long companyId;
    private String name;
    private String address;
}

