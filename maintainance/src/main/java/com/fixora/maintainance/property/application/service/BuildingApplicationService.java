package com.fixora.maintainance.property.application.service;

import com.fixora.maintainance.property.application.mapper.BuildingRequestMapper;
import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.service.BuildingService;
import com.fixora.maintainance.property.inbound.model.BuildingRequestDTO;
import org.springframework.stereotype.Service;

@Service
public class BuildingApplicationService {

    private final BuildingService buildingService;

    public BuildingApplicationService(BuildingService buildingService) {
        this.buildingService = buildingService;
    }

    public Building addBuilding(BuildingRequestDTO buildingRequestDTO){
        return buildingService.addBuilding(BuildingRequestMapper.toDomainRequest(buildingRequestDTO));
    }
}

