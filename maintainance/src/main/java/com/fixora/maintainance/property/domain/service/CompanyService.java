package com.fixora.maintainance.property.domain.service;

import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.requests.CompanyRequest;

public interface CompanyService {

    Company createCompany(CompanyRequest companyRequest);


}

