package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Building;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.IBuildingRepository;

public class DefaultBuildingRequest implements BuildingService{
    private final IBuildingRepository buildingRepository;

    public DefaultBuildingRequest(IBuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Building addBuilding(BuildingRequest buildingRequest) {
        return buildingRepository.addBuilding(buildingRequest);
    }
}
