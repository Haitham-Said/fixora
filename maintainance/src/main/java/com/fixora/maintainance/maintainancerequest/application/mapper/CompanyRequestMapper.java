package com.fixora.maintainance.maintainancerequest.application.mapper;

import com.fixora.maintainance.maintainancerequest.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.maintainancerequest.inbound.model.CompanyRequestDTO;

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
