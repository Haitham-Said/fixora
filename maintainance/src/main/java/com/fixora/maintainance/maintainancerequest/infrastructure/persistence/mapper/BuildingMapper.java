package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.Building;

public class BuildingMapper {

    public static Building toDomain(com.fixora.maintainance.user.infrastructure.entity.shared.Building buildingEntity){
        return Building.builder()
                .id(buildingEntity.getId())
                .address(buildingEntity.getAddress())
                .name(buildingEntity.getName())
                .build();
    }
}
