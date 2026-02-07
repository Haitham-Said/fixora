package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.requests.BuildingRequest;

public interface BuildingService {

    Building addBuilding(BuildingRequest buildingRequest);
    Building getBuildingByBuildingCode(String buildingCode);
    Building getBuildingById(Long buildingId);
}

