package com.fixora.maintainance.property.domain.repository;

import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.requests.CompanyRequest;

public interface ICompanyRepository {

    Company addCompany(CompanyRequest companyRequest);
    Company findByCompanyCode(String companyCode);
    java.util.Optional<Company> findById(long companyId);
}

