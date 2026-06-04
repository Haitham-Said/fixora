package com.fixora.maintainance.property.inbound.model;

import com.fixora.maintainance.property.domain.model.CompanyType;

public record CompanyRequestDTO(
     String name,
     String email,
     String phone,
     String address,
     String companyCode,
     CompanyType type
){}

