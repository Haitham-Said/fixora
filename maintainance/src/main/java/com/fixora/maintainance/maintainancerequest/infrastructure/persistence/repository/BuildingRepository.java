package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Building;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.BuildingRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.IBuildingRepository;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper.BuildingMapper;
import com.fixora.maintainance.user.infrastructure.entity.shared.Company;
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
        com.fixora.maintainance.user.infrastructure.entity.shared.Building buildingEntity=new com.fixora.maintainance.user.infrastructure.entity.shared.Building();
        buildingEntity.setAddress(buildingRequest.getAddress());
        buildingEntity.setName(buildingRequest.getName());
        Company company=new Company();
        company.setId(buildingRequest.getCompanyId());

        buildingEntity.setCompany(company);
        buildingJPARepository.save(buildingEntity);
        return BuildingMapper.toDomain(buildingEntity);
    }
}
