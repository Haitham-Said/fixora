package com.fixora.maintainance.property.domain.model.requests;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BuildingRequest {
    private String companyCode;  // Used for lookup in domain service
    private Long companyId;      // Used by repository after resolution
    private String buildingCode;
    private String name;
    private String address;
}

