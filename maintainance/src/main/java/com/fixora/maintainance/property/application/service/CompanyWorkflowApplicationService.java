package com.fixora.maintainance.property.application.service;

import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.CompanyType;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.PropertyCompanyPaymentMode;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.repository.ICompanyRepository;
import com.fixora.maintainance.property.domain.repository.ICompanyWorkflowConfigRepository;
import com.fixora.maintainance.property.domain.validation.CompanyWorkflowConfigValidator;
import com.fixora.maintainance.property.inbound.model.CompanyWorkflowConfigDto;
import com.fixora.security.application.model.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyWorkflowApplicationService {

    private final ICompanyWorkflowConfigRepository companyWorkflowConfigRepository;
    private final ICompanyRepository companyRepository;

    public CompanyWorkflowApplicationService(
            ICompanyWorkflowConfigRepository companyWorkflowConfigRepository,
            ICompanyRepository companyRepository) {
        this.companyWorkflowConfigRepository = companyWorkflowConfigRepository;
        this.companyRepository = companyRepository;
    }

    private void assertPmCompanyScope(UserInfo user, long pmCompanyId) {
        if ("OPERATION".equalsIgnoreCase(user.role())) {
            return;
        }
        if (user.companyId() == null || !user.companyId().equals(pmCompanyId)) {
            throw new IllegalArgumentException("Company scope mismatch");
        }
        if (!"ADMIN".equalsIgnoreCase(user.role())) {
            throw new IllegalArgumentException("Only PROPERTY_MANAGEMENT ADMIN may read PM workflow config");
        }
    }

    @Transactional(readOnly = true)
    public CompanyWorkflowConfig get(long pmCompanyId, UserInfo user) {
        assertPmCompanyScope(user, pmCompanyId);
        return companyWorkflowConfigRepository.requireByPmCompanyId(pmCompanyId);
    }

    @Transactional
    public CompanyWorkflowConfig upsert(long pmCompanyId, UserInfo user, CompanyWorkflowConfigDto dto) {
        if (!"OPERATION".equalsIgnoreCase(user.role())) {
            throw new IllegalArgumentException("Only OPERATION may change workflow configuration");
        }
        Company pmCompany = companyRepository.findById(pmCompanyId)
                .orElseThrow(() -> new IllegalArgumentException("PM company not found: " + pmCompanyId));

        BusinessMaintenanceModel model = dto.businessMaintenanceModel() != null
                ? dto.businessMaintenanceModel()
                : BusinessMaintenanceModel.INTERNAL_MAINTENANCE;
        Long fmId = dto.facilityManagementCompanyId();
        Company fmCompany = fmId != null ? companyRepository.findById(fmId).orElse(null) : null;

        CompanyWorkflowConfig domain = CompanyWorkflowConfig.builder()
                .pmCompanyId(pmCompanyId)
                .businessMaintenanceModel(model)
                .facilityManagementCompanyId(fmId)
                .estimationRequired(dto.estimationRequired())
                .approvalRequired(dto.approvalRequired())
                .approvalActor(dto.approvalActor() != null ? dto.approvalActor() : WorkflowApprovalActor.NONE)
                .tenantPaymentThreshold(dto.tenantPaymentThreshold())
                .propertyCompanyPaymentMode(dto.propertyCompanyPaymentMode() != null
                        ? dto.propertyCompanyPaymentMode()
                        : PropertyCompanyPaymentMode.NOT_REQUIRED_FOR_MVP)
                .tenantPrepaymentRequired(dto.tenantPrepaymentRequired())
                .build();

        CompanyWorkflowConfigValidator.validate(domain, pmCompany, fmCompany);
        return companyWorkflowConfigRepository.save(domain);
    }
}
