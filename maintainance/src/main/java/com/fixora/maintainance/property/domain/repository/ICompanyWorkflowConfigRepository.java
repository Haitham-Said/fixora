package com.fixora.maintainance.property.domain.repository;

import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;

import java.util.Optional;

public interface ICompanyWorkflowConfigRepository {

    Optional<CompanyWorkflowConfig> findByPmCompanyId(long pmCompanyId);

    CompanyWorkflowConfig requireByPmCompanyId(long pmCompanyId);

    CompanyWorkflowConfig save(CompanyWorkflowConfig config);
}
