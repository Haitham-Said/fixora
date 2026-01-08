package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.property.domain.repository.IBuildingRepository;

public class DefaultBuildingService implements BuildingService{
    private final IBuildingRepository buildingRepository;

    public DefaultBuildingService(IBuildingRepository buildingRepository) {
        this.buildingRepository = buildingRepository;
    }

    @Override
    public Building addBuilding(BuildingRequest buildingRequest) {
        return buildingRepository.addBuilding(buildingRequest);
    }

    @Override
    public Building getBuildingByBuildingCode(String buildingCode) {
        return buildingRepository.findByBuildingCode(buildingCode);
    }
}

