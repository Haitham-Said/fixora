package com.fixora.maintainance.property.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Building {

    private Long id;
    private String name;
    private String address;
    private String buildingCode;
    
    // Reference to company ID instead of entity to maintain domain purity
    private Long companyId;

}

