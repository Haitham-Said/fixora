package com.fixora.maintainance.property.infrastructure.persistence.mapper;

import com.fixora.maintainance.property.domain.model.Building;

public class BuildingMapper {

    public static Building toDomain(com.fixora.maintainance.property.infrastructure.entity.Building buildingEntity){
        return Building.builder()
                .id(buildingEntity.getId())
                .address(buildingEntity.getAddress())
                .name(buildingEntity.getName())
                .buildingCode(buildingEntity.getBuildingCode())
                .companyId(buildingEntity.getCompany() != null ? buildingEntity.getCompany().getId() : null)
                .build();
    }
}

