package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.mapper.BuildingRequestMapper;
import com.fixora.maintainance.maintainancerequest.domain.model.Building;
import com.fixora.maintainance.maintainancerequest.domain.service.BuildingService;
import com.fixora.maintainance.maintainancerequest.inbound.model.BuildingRequestDTO;
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
