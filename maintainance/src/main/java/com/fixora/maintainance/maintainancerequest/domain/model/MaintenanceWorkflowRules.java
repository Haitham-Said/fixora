package com.fixora.maintainance.maintainancerequest.domain.model;

import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.model.PropertyCompanyPaymentMode;
import com.fixora.maintainance.property.domain.model.WorkflowApprovalActor;
import com.fixora.maintainance.property.domain.model.WorkflowEstimationActor;

import java.math.BigDecimal;
import java.util.Optional;

public final class MaintenanceWorkflowRules {

    private MaintenanceWorkflowRules() {
    }

    public static boolean canAssign(
            CompanyWorkflowConfig config,
            BigDecimal estimatedAmount,
            TicketApprovalStatus approvalStatus,
            TicketPaymentStatus paymentStatus
    ) {
        if (config.requiresPortalEstimation()) {
            if (estimatedAmount == null || estimatedAmount.signum() <= 0) {
                return false;
            }
        }
        if (config.isApprovalRequired() && config.getApprovalActor() == WorkflowApprovalActor.PROPERTY_ADMIN) {
            if (approvalStatus != TicketApprovalStatus.APPROVED) {
                return false;
            }
        }
        if (config.isTenantPrepaymentRequired()) {
            if (paymentStatus != TicketPaymentStatus.PAID && paymentStatus != TicketPaymentStatus.COMPANY_BILLED) {
                return false;
            }
        }
        return true;
    }

    public static boolean mayEstimate(
            String userRoleUpper,
            Long userCompanyId,
            WorkflowEstimationActor ticketEstimationActor,
            long executorCompanyId
    ) {
        if (ticketEstimationActor == null || userCompanyId == null || userCompanyId != executorCompanyId) {
            return false;
        }
        String role = userRoleUpper != null ? userRoleUpper.trim().toUpperCase() : "";
        return switch (ticketEstimationActor) {
            case PROPERTY_ADMIN -> "ADMIN".equals(role) || "OPERATION".equals(role);
            case FACILITY_ADMIN -> "FM_ADMIN".equals(role) || "OPERATION".equals(role);
            case NONE -> false;
        };
    }

    /** Portal estimation queue: restrict by actor for PM/FM admins; OPERATION sees all. */
    public static Optional<WorkflowEstimationActor> estimationActorForPortalRole(String userRoleUpper) {
        String role = userRoleUpper != null ? userRoleUpper.trim().toUpperCase() : "";
        return switch (role) {
            case "ADMIN" -> Optional.of(WorkflowEstimationActor.PROPERTY_ADMIN);
            case "FM_ADMIN" -> Optional.of(WorkflowEstimationActor.FACILITY_ADMIN);
            default -> Optional.empty();
        };
    }

    public static boolean mayApprove(String userRoleUpper, long userPmCompanyId, long ticketPmCompanyId, CompanyWorkflowConfig config) {
        if (!config.isApprovalRequired() || config.getApprovalActor() != WorkflowApprovalActor.PROPERTY_ADMIN) {
            return false;
        }
        if (userPmCompanyId != ticketPmCompanyId) {
            return false;
        }
        String role = userRoleUpper != null ? userRoleUpper.trim().toUpperCase() : "";
        return "ADMIN".equals(role) || "OPERATION".equals(role);
    }

    public static TicketStatus statusAfterEstimation(CompanyWorkflowConfig config, BigDecimal estimatedAmount) {
        boolean needsApproval = config.isApprovalRequired()
                && config.getApprovalActor() == WorkflowApprovalActor.PROPERTY_ADMIN
                && (config.getTenantPaymentThreshold() == null
                || estimatedAmount.compareTo(config.getTenantPaymentThreshold()) >= 0);
        if (needsApproval) {
            return TicketStatus.AWAITING_APPROVAL;
        }
        if (config.getPropertyCompanyPaymentMode() == PropertyCompanyPaymentMode.COMPANY_BILLED) {
            return TicketStatus.READY_TO_ASSIGN;
        }
        if (config.isTenantPrepaymentRequired()) {
            return TicketStatus.AWAITING_TENANT_PAYMENT;
        }
        return TicketStatus.READY_TO_ASSIGN;
    }

    public record EstimationOutcome(
            TicketStatus ticketStatus,
            TicketApprovalStatus approvalStatus,
            TicketPaymentStatus paymentStatus,
            TicketPaymentPayerType payerType
    ) {
        public static EstimationOutcome from(CompanyWorkflowConfig config, BigDecimal estimatedAmount) {
            TicketStatus st = statusAfterEstimation(config, estimatedAmount);
            if (st == TicketStatus.AWAITING_APPROVAL) {
                return new EstimationOutcome(st, TicketApprovalStatus.PENDING, TicketPaymentStatus.NOT_REQUIRED, TicketPaymentPayerType.NONE);
            }
            if (st == TicketStatus.AWAITING_TENANT_PAYMENT) {
                return new EstimationOutcome(st, TicketApprovalStatus.NOT_REQUIRED, TicketPaymentStatus.PENDING, TicketPaymentPayerType.TENANT);
            }
            if (config.getPropertyCompanyPaymentMode() == PropertyCompanyPaymentMode.COMPANY_BILLED) {
                return new EstimationOutcome(st, TicketApprovalStatus.NOT_REQUIRED, TicketPaymentStatus.COMPANY_BILLED, TicketPaymentPayerType.PROPERTY_COMPANY);
            }
            return new EstimationOutcome(st, TicketApprovalStatus.NOT_REQUIRED, TicketPaymentStatus.NOT_REQUIRED, TicketPaymentPayerType.NONE);
        }
    }

    public static TicketStatus statusAfterApproval(CompanyWorkflowConfig config) {
        if (config.getPropertyCompanyPaymentMode() == PropertyCompanyPaymentMode.COMPANY_BILLED) {
            return TicketStatus.READY_TO_ASSIGN;
        }
        if (config.isTenantPrepaymentRequired()) {
            return TicketStatus.AWAITING_TENANT_PAYMENT;
        }
        return TicketStatus.READY_TO_ASSIGN;
    }

    public record ApprovalOutcome(TicketStatus ticketStatus, TicketPaymentStatus paymentStatus, TicketPaymentPayerType payerType) {
        public static ApprovalOutcome from(CompanyWorkflowConfig config) {
            TicketStatus st = statusAfterApproval(config);
            if (st == TicketStatus.AWAITING_TENANT_PAYMENT) {
                return new ApprovalOutcome(st, TicketPaymentStatus.PENDING, TicketPaymentPayerType.TENANT);
            }
            if (config.getPropertyCompanyPaymentMode() == PropertyCompanyPaymentMode.COMPANY_BILLED) {
                return new ApprovalOutcome(st, TicketPaymentStatus.COMPANY_BILLED, TicketPaymentPayerType.PROPERTY_COMPANY);
            }
            return new ApprovalOutcome(st, TicketPaymentStatus.NOT_REQUIRED, TicketPaymentPayerType.NONE);
        }
    }

    public static TicketStatus statusAfterPayment() {
        return TicketStatus.READY_TO_ASSIGN;
    }
}
