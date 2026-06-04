package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.CompanyType;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.requests.CompanyRequest;
import com.fixora.maintainance.property.domain.repository.ICompanyRepository;
import com.fixora.maintainance.property.domain.repository.ICompanyWorkflowConfigRepository;
import org.springframework.transaction.annotation.Transactional;

public class DefaultCompanyService implements CompanyService{
    private final ICompanyRepository companyRepository;
    private final ICompanyWorkflowConfigRepository companyWorkflowConfigRepository;

    public DefaultCompanyService(ICompanyRepository companyRepository,
                                 ICompanyWorkflowConfigRepository companyWorkflowConfigRepository) {
        this.companyRepository = companyRepository;
        this.companyWorkflowConfigRepository = companyWorkflowConfigRepository;
    }

    @Override
    @Transactional
    public Company createCompany(CompanyRequest companyRequest) {
        Company company = companyRepository.addCompany(companyRequest);
        if (company.getType() == CompanyType.PROPERTY_MANAGEMENT) {
            companyWorkflowConfigRepository.save(CompanyWorkflowConfig.baselineForPmCompany(company.getId()));
        }
        return company;
    }
}

