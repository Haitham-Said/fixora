package com.fixora.maintainance.maintainancerequest.domain.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.CompanyRequest;

public interface ICompanyRepository {

    Company addCompany(CompanyRequest companyRequest);
}
