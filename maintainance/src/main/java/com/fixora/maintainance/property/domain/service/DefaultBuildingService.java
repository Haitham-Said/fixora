package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.property.domain.repository.IBuildingRepository;
import com.fixora.maintainance.property.domain.repository.ICompanyRepository;

public class DefaultBuildingService implements BuildingService{
    private final IBuildingRepository buildingRepository;
    private final ICompanyRepository companyRepository;

    public DefaultBuildingService(IBuildingRepository buildingRepository, ICompanyRepository companyRepository) {
        this.buildingRepository = buildingRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    public Building addBuilding(BuildingRequest buildingRequest) {
        // Business logic: Validate company exists by code
        Company company = companyRepository.findByCompanyCode(buildingRequest.getCompanyCode());
        if (company == null) {
            throw new IllegalArgumentException("Company not found with code: " + buildingRequest.getCompanyCode());
        }

        // Create new request with resolved company ID for repository
        BuildingRequest requestWithCompanyId = BuildingRequest.builder()
                .companyId(company.getId())
                .name(buildingRequest.getName())
                .address(buildingRequest.getAddress())
                .build();

        return buildingRepository.addBuilding(requestWithCompanyId);
    }

    @Override
    public Building getBuildingByBuildingCode(String buildingCode) {
        return buildingRepository.findByBuildingCode(buildingCode);
    }
}

