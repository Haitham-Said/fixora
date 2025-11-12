package com.fixora.maintainance.maintainancerequest.inbound.model;

import lombok.Data;

@Data
public class BuildingRequestDTO {
    private Long companyId;
    private String name;
    private String address;
}
