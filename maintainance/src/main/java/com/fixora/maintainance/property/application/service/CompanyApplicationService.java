package com.fixora.maintainance.property.application.service;

import com.fixora.maintainance.property.application.mapper.CompanyRequestMapper;
import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.service.CompanyService;
import com.fixora.maintainance.property.inbound.model.CompanyRequestDTO;

import org.springframework.stereotype.Service;

@Service
public class CompanyApplicationService {

    private final CompanyService companyService;

    public CompanyApplicationService(CompanyService companyService) {
        this.companyService = companyService;
    }

    public Company addCompany(CompanyRequestDTO companyRequestDTO) {
        return companyService.createCompany(
                CompanyRequestMapper.toDomainRequest(companyRequestDTO)
        );
    }
}

