package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Building;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.BuildingRequest;

public interface BuildingService {

    Building addBuilding(BuildingRequest buildingRequest);
}
