package com.fixora.maintainance.maintainancerequest.application.routing;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TicketRoutingServiceTest {

    private final TicketRoutingService routingService = new TicketRoutingService(
            List.of(new InternalMaintenanceRoutingStrategy(), new FacilityManagementRoutingStrategy()));

    @Test
    void internalMaintenance_routesToPmExecutor() {
        var decision = routingService.route(10L, CompanyWorkflowConfig.builder()
                .pmCompanyId(10L)
                .businessMaintenanceModel(BusinessMaintenanceModel.INTERNAL_MAINTENANCE)
                .estimationRequired(true)
                .approvalRequired(true)
                .approvalActor(WorkflowApprovalActor.PROPERTY_ADMIN)
                .build());

        assertEquals(10L, decision.pmCompanyId());
        assertEquals(10L, decision.executorCompanyId());
        assertNull(decision.facilityManagementCompanyId());
        assertEquals(TicketStatus.NEEDS_ESTIMATION, decision.initialStatus());
        assertEquals(WorkflowEstimationActor.PROPERTY_ADMIN, decision.estimationActor());
    }

    @Test
    void facilityManagement_routesToFmExecutor() {
        var decision = routingService.route(10L, CompanyWorkflowConfig.builder()
                .pmCompanyId(10L)
                .businessMaintenanceModel(BusinessMaintenanceModel.FACILITY_MANAGEMENT)
                .facilityManagementCompanyId(99L)
                .estimationRequired(true)
                .approvalRequired(true)
                .approvalActor(WorkflowApprovalActor.PROPERTY_ADMIN)
                .build());

        assertEquals(10L, decision.pmCompanyId());
        assertEquals(99L, decision.executorCompanyId());
        assertEquals(99L, decision.facilityManagementCompanyId());
        assertEquals(WorkflowEstimationActor.FACILITY_ADMIN, decision.estimationActor());
    }
}
