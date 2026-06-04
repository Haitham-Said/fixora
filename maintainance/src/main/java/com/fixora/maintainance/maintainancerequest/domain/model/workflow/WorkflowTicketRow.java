package com.fixora.maintainance.maintainancerequest.domain.model.workflow;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketApprovalStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;

import java.math.BigDecimal;

public record WorkflowTicketRow(
        long id,
        long pmCompanyId,
        long executorCompanyId,
        Long facilityManagementCompanyId,
        TicketStatus status,
        WorkflowEstimationActor ticketEstimationActor,
        BigDecimal estimatedAmount,
        String estimationNote,
        boolean approved,
        boolean paid,
        String paymentRef,
        String approvalActor,
        TicketApprovalStatus approvalStatus,
        TicketPaymentStatus paymentStatus,
        Long customerUserId,
        String customerPhone
) {
}
