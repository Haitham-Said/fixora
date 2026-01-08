package com.fixora.maintainance.property.application.mapper;

import com.fixora.maintainance.property.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.property.inbound.model.CompanyRequestDTO;

public class CompanyRequestMapper {

    public static CompanyRequest toDomainRequest(CompanyRequestDTO companyRequestDTO){
        return CompanyRequest.builder()
                .name(companyRequestDTO.name())
                .email(companyRequestDTO.email())
                .phone(companyRequestDTO.phone())
                .address(companyRequestDTO.address())
                .build();

    }
}

