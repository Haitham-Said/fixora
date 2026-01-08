package com.fixora.maintainance.property.domain.repository;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.requests.BuildingRequest;

public interface IBuildingRepository {
    Building addBuilding(BuildingRequest buildingRequestRequest);
    Building findByBuildingCode(String buildingCode);
}

