package com.fixora.maintainance.maintainancerequest.domain.model;

import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.PropertyCompanyPaymentMode;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceWorkflowRulesTest {

    @Test
    void mayEstimate_fmAdminOnExecutorCompany() {
        assertTrue(MaintenanceWorkflowRules.mayEstimate(
                "FM_ADMIN", 99L, WorkflowEstimationActor.FACILITY_ADMIN, 99L));
        assertFalse(MaintenanceWorkflowRules.mayEstimate(
                "FM_ADMIN", 99L, WorkflowEstimationActor.FACILITY_ADMIN, 10L));
    }

    @Test
    void estimationActorForPortalRole_mapsAdminAndFmAdmin() {
        assertEquals(WorkflowEstimationActor.PROPERTY_ADMIN,
                MaintenanceWorkflowRules.estimationActorForPortalRole("ADMIN").orElseThrow());
        assertEquals(WorkflowEstimationActor.FACILITY_ADMIN,
                MaintenanceWorkflowRules.estimationActorForPortalRole("fm_admin").orElseThrow());
        assertTrue(MaintenanceWorkflowRules.estimationActorForPortalRole("OPERATION").isEmpty());
    }

    @Test
    void mayApprove_pmAdminOnPmCompany() {
        CompanyWorkflowConfig cfg = CompanyWorkflowConfig.builder()
                .pmCompanyId(10L)
                .businessMaintenanceModel(BusinessMaintenanceModel.FACILITY_MANAGEMENT)
                .facilityManagementCompanyId(99L)
                .approvalRequired(true)
                .approvalActor(WorkflowApprovalActor.PROPERTY_ADMIN)
                .build();
        assertTrue(MaintenanceWorkflowRules.mayApprove("ADMIN", 10L, 10L, cfg));
        assertFalse(MaintenanceWorkflowRules.mayApprove("FM_ADMIN", 99L, 10L, cfg));
    }

    @Test
    void canAssign_requiresEstimateWhenConfigured() {
        CompanyWorkflowConfig cfg = CompanyWorkflowConfig.builder()
                .pmCompanyId(1L)
                .businessMaintenanceModel(BusinessMaintenanceModel.INTERNAL_MAINTENANCE)
                .estimationRequired(true)
                .approvalRequired(false)
                .approvalActor(WorkflowApprovalActor.NONE)
                .propertyCompanyPaymentMode(PropertyCompanyPaymentMode.NOT_REQUIRED_FOR_MVP)
                .tenantPrepaymentRequired(false)
                .build();
        assertFalse(MaintenanceWorkflowRules.canAssign(cfg, null,
                TicketApprovalStatus.NOT_REQUIRED, TicketPaymentStatus.NOT_REQUIRED));
        assertTrue(MaintenanceWorkflowRules.canAssign(cfg, BigDecimal.TEN,
                TicketApprovalStatus.NOT_REQUIRED, TicketPaymentStatus.NOT_REQUIRED));
    }
}
