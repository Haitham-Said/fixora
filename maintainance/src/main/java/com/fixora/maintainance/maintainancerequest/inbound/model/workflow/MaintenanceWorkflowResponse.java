package com.fixora.maintainance.maintainancerequest.inbound.model.workflow;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketApprovalStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentPayerType;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;

import java.math.BigDecimal;

/**
 * API read model for workflow screen (not a JPA entity).
 */
public record MaintenanceWorkflowResponse(
        long ticketId,
        TicketStatus status,
        Long pmCompanyId,
        Long executorCompanyId,
        Long facilityManagementCompanyId,
        BigDecimal estimatedAmount,
        String estimationNote,
        boolean approved,
        boolean paid,
        String paymentRef,
        String approvalActor,
        TicketApprovalStatus approvalStatus,
        TicketPaymentStatus paymentStatus,
        TicketPaymentPayerType payerType,
        boolean canAssign,
        boolean estimationRequired,
        BusinessMaintenanceModel businessMaintenanceModel,
        boolean approvalRequired,
        WorkflowApprovalActor configuredApprovalActor,
        BigDecimal tenantPaymentThreshold,
        boolean tenantPrepaymentRequired
) {
    public static MaintenanceWorkflowResponse from(Ticket t, CompanyWorkflowConfig cfg, boolean canAssign) {
        return new MaintenanceWorkflowResponse(
                t.getId(),
                t.getStatus(),
                t.getPmCompanyId() != null ? t.getPmCompanyId() : t.getCompanyId(),
                t.getExecutorCompanyId(),
                t.getFacilityManagementCompanyId(),
                t.getEstimatedAmount(),
                t.getEstimationNote(),
                t.isApproved(),
                t.isPaid(),
                t.getPaymentRef(),
                t.getApprovalActor(),
                t.getTicketApprovalStatus() != null ? t.getTicketApprovalStatus() : TicketApprovalStatus.NOT_REQUIRED,
                t.getTicketPaymentStatus() != null ? t.getTicketPaymentStatus() : TicketPaymentStatus.NOT_REQUIRED,
                t.getPayerType() != null ? t.getPayerType() : TicketPaymentPayerType.NONE,
                canAssign,
                cfg.isEstimationRequired(),
                cfg.getBusinessMaintenanceModel(),
                cfg.isApprovalRequired(),
                cfg.getApprovalActor(),
                cfg.getTenantPaymentThreshold(),
                cfg.isTenantPrepaymentRequired()
        );
    }
}
