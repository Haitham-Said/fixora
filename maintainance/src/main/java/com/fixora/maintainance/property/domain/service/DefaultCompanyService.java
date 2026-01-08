package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.property.domain.repository.ICompanyRepository;

public class DefaultCompanyService implements CompanyService{
    private final ICompanyRepository companyRepository;

    public DefaultCompanyService(ICompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public Company createCompany(CompanyRequest companyRequest) {
        return companyRepository.addCompany(companyRequest);
    }
}

