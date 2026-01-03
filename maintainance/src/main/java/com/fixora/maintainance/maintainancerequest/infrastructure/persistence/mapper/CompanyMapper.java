package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.Company;

import java.util.Collections;
import java.util.stream.Collectors;

public class CompanyMapper {

    public static Company toDomain(com.fixora.maintainance.user.infrastructure.entity.shared.Company companyEntity) {
        return Company.builder()
                .id(companyEntity.getId())
                .phone(companyEntity.getPhone())
                .email(companyEntity.getEmail())
                .name(companyEntity.getName())
                .address(companyEntity.getAddress())
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
