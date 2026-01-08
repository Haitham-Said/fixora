package com.fixora.maintainance.property.application.mapper;

import com.fixora.maintainance.property.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.property.inbound.model.BuildingRequestDTO;

public class BuildingRequestMapper {

    public static BuildingRequest toDomainRequest(BuildingRequestDTO buildingRequestDTO){
        return BuildingRequest.builder()
                .companyId(buildingRequestDTO.getCompanyId())
                .name(buildingRequestDTO.getName())
                .address(buildingRequestDTO.getAddress())
                .build();
    }
}

