package com.fixora.maintainance.property.infrastructure.persistence.repository;

import com.fixora.maintainance.property.domain.exception.CompanyWorkflowConfigNotFoundException;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.PropertyCompanyPaymentMode;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.repository.ICompanyWorkflowConfigRepository;
import com.fixora.maintainance.property.infrastructure.entity.CompanyWorkflowConfigEntity;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class CompanyWorkflowConfigRepository implements ICompanyWorkflowConfigRepository {

    private final CompanyWorkflowConfigJpaRepository jpaRepository;

    public CompanyWorkflowConfigRepository(CompanyWorkflowConfigJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<CompanyWorkflowConfig> findByPmCompanyId(long pmCompanyId) {
        return jpaRepository.findById(pmCompanyId).map(this::toDomain);
    }

    @Override
    public CompanyWorkflowConfig requireByPmCompanyId(long pmCompanyId) {
        return findByPmCompanyId(pmCompanyId)
                .orElseThrow(() -> new CompanyWorkflowConfigNotFoundException(pmCompanyId));
    }

    @Override
    @Transactional
    public CompanyWorkflowConfig save(CompanyWorkflowConfig config) {
        CompanyWorkflowConfigEntity entity = jpaRepository.findById(config.getPmCompanyId())
                .orElse(new CompanyWorkflowConfigEntity());
        entity.setPmCompanyId(config.getPmCompanyId());
        entity.setBusinessMaintenanceModel(config.getBusinessMaintenanceModel());
        entity.setFacilityManagementCompanyId(config.getFacilityManagementCompanyId());
        entity.setEstimationRequired(config.isEstimationRequired());
        entity.setApprovalRequired(config.isApprovalRequired());
        entity.setWorkflowApprovalActor(config.getApprovalActor() != null
                ? config.getApprovalActor()
                : WorkflowApprovalActor.NONE);
        entity.setApprovalThreshold(config.getTenantPaymentThreshold());
        entity.setPaymentRequired(config.isTenantPrepaymentRequired());
        entity.setPropertyCompanyPaymentMode(config.getPropertyCompanyPaymentMode() != null
                ? config.getPropertyCompanyPaymentMode()
                : PropertyCompanyPaymentMode.NOT_REQUIRED_FOR_MVP);
        entity.setUpdatedAt(LocalDateTime.now());
        return toDomain(jpaRepository.save(entity));
    }

    private CompanyWorkflowConfig toDomain(CompanyWorkflowConfigEntity e) {
        return CompanyWorkflowConfig.builder()
                .pmCompanyId(e.getPmCompanyId())
                .businessMaintenanceModel(e.getBusinessMaintenanceModel())
                .facilityManagementCompanyId(e.getFacilityManagementCompanyId())
                .estimationRequired(e.isEstimationRequired())
                .approvalRequired(e.isApprovalRequired())
                .approvalActor(e.getWorkflowApprovalActor())
                .tenantPaymentThreshold(e.getApprovalThreshold())
                .propertyCompanyPaymentMode(e.getPropertyCompanyPaymentMode())
                .tenantPrepaymentRequired(e.isPaymentRequired())
                .build();
    }
}
