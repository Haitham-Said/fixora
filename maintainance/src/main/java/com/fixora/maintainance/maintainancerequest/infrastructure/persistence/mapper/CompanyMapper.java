package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.Company;

public class CompanyMapper {

    public static Company toDomain(com.fixora.maintainance.user.infrastructure.entity.shared.Company companyEntity) {
        return Company.builder()
                .id(companyEntity.getId())
                .phone(companyEntity.getPhone())
                .email(companyEntity.getEmail())
                .userEntities(companyEntity.getUsers())
                .name(companyEntity.getName())
                .address(companyEntity.getAddress())
                .buildings(companyEntity.getBuildings())
                .createdAt(companyEntity.getCreatedAt())
                .build();
    }
}
