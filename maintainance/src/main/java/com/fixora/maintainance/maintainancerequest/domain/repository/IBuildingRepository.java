package com.fixora.maintainance.maintainancerequest.domain.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Building;
import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.CompanyRequest;

public interface IBuildingRepository {
    Building addBuilding(BuildingRequest buildingRequestRequest);
}
