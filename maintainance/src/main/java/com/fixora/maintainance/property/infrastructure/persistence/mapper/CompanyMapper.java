package com.fixora.maintainance.property.infrastructure.persistence.mapper;

import com.fixora.maintainance.property.domain.model.Company;

import java.util.Collections;
import java.util.stream.Collectors;

public class CompanyMapper {

    public static Company toDomain(com.fixora.maintainance.property.infrastructure.entity.Company companyEntity) {
        return Company.builder()
                .id(companyEntity.getId())
                .phone(companyEntity.getPhone())
                .email(companyEntity.getEmail())
                .name(companyEntity.getName())
                .address(companyEntity.getAddress())
                .companyCode(companyEntity.getCompanyCode())
                .createdAt(companyEntity.getCreatedAt())
                .userIds(companyEntity.getUsers() != null ? 
                    companyEntity.getUsers().stream()
                        .map(user -> user.getId())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .buildingIds(companyEntity.getBuildings() != null ?
                    companyEntity.getBuildings().stream()
                        .map(building -> building.getId())
                        .collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }
}

