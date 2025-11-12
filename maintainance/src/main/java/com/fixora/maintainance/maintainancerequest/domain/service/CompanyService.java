package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Company;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.CompanyRequest;

public interface CompanyService {

    Company createCompany(CompanyRequest companyRequest);


}
