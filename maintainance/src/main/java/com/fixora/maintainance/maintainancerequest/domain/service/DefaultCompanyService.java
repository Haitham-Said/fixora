package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.ICompanyRepository;
import org.springframework.stereotype.Service;

@Service
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
