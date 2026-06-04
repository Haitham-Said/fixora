package com.fixora.maintainance.property.inbound.model;

import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.PropertyCompanyPaymentMode;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;

import java.math.BigDecimal;

public record CompanyWorkflowConfigDto(
        BusinessMaintenanceModel businessMaintenanceModel,
        Long facilityManagementCompanyId,
        boolean estimationRequired,
        boolean approvalRequired,
        WorkflowApprovalActor approvalActor,
        BigDecimal tenantPaymentThreshold,
        PropertyCompanyPaymentMode propertyCompanyPaymentMode,
        boolean tenantPrepaymentRequired
) {
}
