package com.fixora.maintainance.property.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Per-PM-company maintenance handling policy. Only {@link CompanyType#PROPERTY_MANAGEMENT} companies have a row.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyWorkflowConfig {

    private Long pmCompanyId;
    private BusinessMaintenanceModel businessMaintenanceModel;
    /** Required when {@link BusinessMaintenanceModel#FACILITY_MANAGEMENT}; must be a FM company id. */
    private Long facilityManagementCompanyId;
    private boolean estimationRequired;
    private boolean approvalRequired;
    private WorkflowApprovalActor approvalActor;
    private BigDecimal tenantPaymentThreshold;
    private PropertyCompanyPaymentMode propertyCompanyPaymentMode;
    private boolean tenantPrepaymentRequired;

    public boolean requiresPortalEstimation() {
        return estimationRequired;
    }

    public static CompanyWorkflowConfig baselineForPmCompany(long pmCompanyId) {
        return CompanyWorkflowConfig.builder()
                .pmCompanyId(pmCompanyId)
                .businessMaintenanceModel(BusinessMaintenanceModel.INTERNAL_MAINTENANCE)
                .facilityManagementCompanyId(null)
                .estimationRequired(false)
                .approvalRequired(false)
                .approvalActor(WorkflowApprovalActor.NONE)
                .tenantPaymentThreshold(null)
                .propertyCompanyPaymentMode(PropertyCompanyPaymentMode.NOT_REQUIRED_FOR_MVP)
                .tenantPrepaymentRequired(false)
                .build();
    }
}
