package com.fixora.maintainance.maintainancerequest.application.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.maintainancerequest.inbound.model.BuildingRequestDTO;

public class BuildingRequestMapper {

    public static BuildingRequest toDomainRequest(BuildingRequestDTO buildingRequestDTO){
        return BuildingRequest.builder()
                .companyId(buildingRequestDTO.getCompanyId())
                .name(buildingRequestDTO.getName())
                .address(buildingRequestDTO.getAddress())
                .build();
    }
}
