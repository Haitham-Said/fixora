package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.domain.model.Building;
import com.fixora.maintainance.property.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.property.domain.repository.IBuildingRepository;
import com.fixora.maintainance.property.infrastructure.persistence.mapper.BuildingMapper;
import com.fixora.maintainance.property.infrastructure.entity.Company;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public class BuildingRepository implements IBuildingRepository {
    private final BuildingJPARepository buildingJPARepository;

    public BuildingRepository(BuildingJPARepository buildingJPARepository) {
        this.buildingJPARepository = buildingJPARepository;
    }

    @Override
    @Transactional
    public Building addBuilding(BuildingRequest buildingRequest) {
        // Repository only handles data persistence - business logic is in domain service
        if (buildingRequest.getCompanyId() == null) {
            throw new IllegalStateException("Company ID must be resolved before persisting building");
        }

        com.fixora.maintainance.property.infrastructure.entity.Building buildingEntity=new com.fixora.maintainance.property.infrastructure.entity.Building();
        buildingEntity.setAddress(buildingRequest.getAddress());
        buildingEntity.setName(buildingRequest.getName());
        buildingEntity.setBuildingCode(buildingRequest.getBuildingCode());
        Company company=new Company();
        company.setId(buildingRequest.getCompanyId());

        buildingEntity.setCompany(company);
        buildingJPARepository.save(buildingEntity);
        return BuildingMapper.toDomain(buildingEntity);
    }

    @Override
    public Building findByBuildingCode(String buildingCode) {
        com.fixora.maintainance.property.infrastructure.entity.Building buildingEntity = buildingJPARepository.findByBuildingCode(buildingCode);
        if (buildingEntity == null) {
            return null;
        }
        return BuildingMapper.toDomain(buildingEntity);
    }
}

