package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.mapper.CompanyRequestMapper;
import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.domain.service.CompanyService;
import com.fixora.maintainance.maintainancerequest.inbound.model.CompanyRequestDTO;

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
