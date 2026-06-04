package com.fixora.maintainance.maintainancerequest.application.routing;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;
import org.springframework.stereotype.Component;

@Component
public class FacilityManagementRoutingStrategy implements MaintenanceRoutingStrategy {

    @Override
    public boolean supports(CompanyWorkflowConfig config) {
        return config.getBusinessMaintenanceModel() == BusinessMaintenanceModel.FACILITY_MANAGEMENT;
    }

    @Override
    public TicketRoutingDecision route(long pmCompanyId, CompanyWorkflowConfig config) {
        Long fmCompanyId = config.getFacilityManagementCompanyId();
        if (fmCompanyId == null) {
            throw new IllegalStateException("FACILITY_MANAGEMENT config missing facilityManagementCompanyId");
        }
        TicketStatus status = config.isEstimationRequired()
                ? TicketStatus.NEEDS_ESTIMATION
                : TicketStatus.READY_TO_ASSIGN;
        WorkflowApprovalActor approvalActor = config.isApprovalRequired()
                ? WorkflowApprovalActor.PROPERTY_ADMIN
                : WorkflowApprovalActor.NONE;
        return TicketRoutingDecision.builder()
                .pmCompanyId(pmCompanyId)
                .executorCompanyId(fmCompanyId)
                .facilityManagementCompanyId(fmCompanyId)
                .initialStatus(status)
                .estimationActor(config.isEstimationRequired() ? WorkflowEstimationActor.FACILITY_ADMIN : null)
                .approvalActor(approvalActor)
                .build();
    }
}
