package com.fixora.maintainance.property.domain.exception;

/**
 * Raised when a company has no {@code company_workflow_config} row. Every company must be onboarded with workflow config.
 */
public class CompanyWorkflowConfigNotFoundException extends RuntimeException {

    private final long companyId;

    public CompanyWorkflowConfigNotFoundException(long companyId) {
        super("Workflow configuration not found for PM company id " + companyId
                + ". Create it via POST /api/companies/{pmCompanyId}/workflow-config (OPERATION).");
        this.companyId = companyId;
    }

    public long getCompanyId() {
        return companyId;
    }
}
