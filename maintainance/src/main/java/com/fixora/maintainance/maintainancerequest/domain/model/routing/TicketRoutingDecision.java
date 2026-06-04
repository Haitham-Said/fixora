package com.fixora.maintainance.maintainancerequest.domain.model.routing;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;
import lombok.Builder;

/**
 * Result of {@link com.fixora.maintainance.maintainancerequest.application.routing.TicketRoutingService}.
 */
@Builder
public record TicketRoutingDecision(
        long pmCompanyId,
        long executorCompanyId,
        Long facilityManagementCompanyId,
        TicketStatus initialStatus,
        WorkflowEstimationActor estimationActor,
        WorkflowApprovalActor approvalActor
) {
}
