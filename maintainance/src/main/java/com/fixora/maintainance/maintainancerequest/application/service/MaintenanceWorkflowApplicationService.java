package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.notification.MaintenanceWorkflowNotifier;
import com.fixora.maintainance.maintainancerequest.application.payment.MaintenancePaymentService;
import com.fixora.maintainance.maintainancerequest.domain.exception.InvalidWorkflowStateException;
import com.fixora.maintainance.maintainancerequest.domain.exception.MaintenanceWorkflowException;
import com.fixora.maintainance.maintainancerequest.domain.exception.WorkflowAccessDeniedException;
import com.fixora.maintainance.maintainancerequest.domain.model.MaintenanceWorkflowRules;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.workflow.WorkflowTicketRow;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.maintainancerequest.inbound.model.workflow.MaintenanceWorkflowResponse;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.workflow.MaintenanceWorkflowTicketRepository;
import com.fixora.maintainance.maintainancerequest.infrastructure.payment.MaintenancePaymentCallbackValidator;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;
import com.fixora.maintainance.property.domain.repository.ICompanyWorkflowConfigRepository;
import com.fixora.security.application.model.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class MaintenanceWorkflowApplicationService {

    private final MaintenanceWorkflowTicketRepository workflowTicketRepository;
    private final ICompanyWorkflowConfigRepository companyWorkflowConfigRepository;
    private final MaintenancePaymentService maintenancePaymentService;
    private final MaintenanceWorkflowNotifier workflowNotifier;
    private final TicketService ticketService;
    private final MaintenancePaymentCallbackValidator paymentCallbackValidator;

    public MaintenanceWorkflowApplicationService(
            MaintenanceWorkflowTicketRepository workflowTicketRepository,
            ICompanyWorkflowConfigRepository companyWorkflowConfigRepository,
            MaintenancePaymentService maintenancePaymentService,
            MaintenanceWorkflowNotifier workflowNotifier,
            TicketService ticketService,
            MaintenancePaymentCallbackValidator paymentCallbackValidator) {
        this.workflowTicketRepository = workflowTicketRepository;
        this.companyWorkflowConfigRepository = companyWorkflowConfigRepository;
        this.maintenancePaymentService = maintenancePaymentService;
        this.workflowNotifier = workflowNotifier;
        this.ticketService = ticketService;
        this.paymentCallbackValidator = paymentCallbackValidator;
    }

    private CompanyWorkflowConfig resolvePmConfig(long pmCompanyId) {
        return companyWorkflowConfigRepository.requireByPmCompanyId(pmCompanyId);
    }

    private WorkflowTicketRow requireTicketRow(long ticketId, UserInfo actor) {
        return workflowTicketRepository.loadRowForPortalActor(ticketId, actor.companyId(), actor.role())
                .orElseThrow(() -> new WorkflowAccessDeniedException("Ticket not visible for this user"));
    }

    @Transactional
    public Ticket submitEstimation(long ticketId, UserInfo actor, BigDecimal amount, String note) {
        WorkflowTicketRow row = requireTicketRow(ticketId, actor);
        CompanyWorkflowConfig cfg = resolvePmConfig(row.pmCompanyId());
        if (!MaintenanceWorkflowRules.mayEstimate(actor.role(), actor.companyId(), row.ticketEstimationActor(), row.executorCompanyId())) {
            throw new WorkflowAccessDeniedException("Current role may not submit estimation for this ticket");
        }
        if (row.status() != TicketStatus.NEEDS_ESTIMATION && row.status() != TicketStatus.CREATED) {
            throw new InvalidWorkflowStateException("Estimation allowed only from NEEDS_ESTIMATION or CREATED");
        }
        if (amount == null || amount.signum() <= 0) {
            throw new MaintenanceWorkflowException("Estimate amount must be positive");
        }
        MaintenanceWorkflowRules.EstimationOutcome outcome = MaintenanceWorkflowRules.EstimationOutcome.from(cfg, amount);
        workflowTicketRepository.applyEstimation(ticketId, row.executorCompanyId(), amount, note, outcome);
        if (outcome.ticketStatus() == TicketStatus.AWAITING_TENANT_PAYMENT) {
            initiatePaymentInternal(requireTicketRow(ticketId, actor));
        }
        return reloadTicket(ticketId);
    }

    @Transactional
    public Ticket approveRequest(long ticketId, UserInfo actor) {
        WorkflowTicketRow row = requireTicketRow(ticketId, actor);
        CompanyWorkflowConfig cfg = resolvePmConfig(row.pmCompanyId());
        if (row.status() != TicketStatus.AWAITING_APPROVAL) {
            throw new InvalidWorkflowStateException("Ticket is not waiting for approval");
        }
        if (!MaintenanceWorkflowRules.mayApprove(actor.role(), actor.companyId(), row.pmCompanyId(), cfg)) {
            throw new WorkflowAccessDeniedException("Only property ADMIN (or OPERATION) may approve for this ticket");
        }
        MaintenanceWorkflowRules.ApprovalOutcome outcome = MaintenanceWorkflowRules.ApprovalOutcome.from(cfg);
        workflowTicketRepository.applyApproval(ticketId, row.pmCompanyId(), actor.userId(), outcome);
        if (outcome.ticketStatus() == TicketStatus.AWAITING_TENANT_PAYMENT) {
            initiatePaymentInternal(requireTicketRow(ticketId, actor));
        }
        return reloadTicket(ticketId);
    }

    @Transactional
    public Ticket rejectRequest(long ticketId, UserInfo actor) {
        WorkflowTicketRow row = requireTicketRow(ticketId, actor);
        CompanyWorkflowConfig cfg = resolvePmConfig(row.pmCompanyId());
        if (row.status() != TicketStatus.AWAITING_APPROVAL) {
            throw new InvalidWorkflowStateException("Ticket is not waiting for approval");
        }
        if (!MaintenanceWorkflowRules.mayApprove(actor.role(), actor.companyId(), row.pmCompanyId(), cfg)) {
            throw new WorkflowAccessDeniedException("Only property ADMIN (or OPERATION) may reject for this ticket");
        }
        workflowTicketRepository.applyRejection(ticketId, row.pmCompanyId());
        return reloadTicket(ticketId);
    }

    private void initiatePaymentInternal(WorkflowTicketRow row) {
        BigDecimal amount = row.estimatedAmount();
        if (amount == null || amount.signum() <= 0) {
            throw new MaintenanceWorkflowException("Cannot initiate payment without positive estimated amount");
        }
        MaintenancePaymentService.PaymentInitResult pay =
                maintenancePaymentService.initiatePayment(row.id(), amount);
        workflowNotifier.notifyPaymentLink(row.customerPhone(), row.id(), pay.paymentUrl(), pay.externalReference());
    }

    @Transactional
    public Ticket markPaidByCallback(String signatureHeader,
                                     com.fixora.maintainance.maintainancerequest.inbound.model.workflow.PaymentCallbackRequestDto body) {
        paymentCallbackValidator.assertValidCallback(signatureHeader, body);
        WorkflowTicketRow row = workflowTicketRepository.loadRow(body.ticketId())
                .orElseThrow(() -> new MaintenanceWorkflowException("Ticket not found"));
        if (row.paymentStatus() == TicketPaymentStatus.PAID) {
            return reloadTicket(body.ticketId());
        }
        if (row.status() != TicketStatus.AWAITING_TENANT_PAYMENT) {
            throw new InvalidWorkflowStateException("Ticket is not waiting for tenant payment");
        }
        if (row.estimatedAmount() == null || body.amount() == null
                || row.estimatedAmount().compareTo(body.amount()) != 0) {
            throw new MaintenanceWorkflowException("Callback amount does not match estimated amount");
        }
        if (!paymentCallbackValidator.isSuccessfulGatewayStatus(body.gatewayStatus())) {
            throw new MaintenanceWorkflowException("Gateway reported non-success payment status");
        }
        if (row.paymentRef() != null && !row.paymentRef().isBlank() && body.paymentRef() != null
                && !Objects.equals(row.paymentRef(), body.paymentRef())) {
            throw new MaintenanceWorkflowException("Payment reference mismatch");
        }
        TicketStatus next = MaintenanceWorkflowRules.statusAfterPayment();
        workflowTicketRepository.applyPaid(body.ticketId(), body.paymentRef(), next);
        WorkflowTicketRow after = workflowTicketRepository.loadRow(body.ticketId()).orElse(row);
        workflowNotifier.notifyPaymentConfirmed(after.customerPhone(), body.ticketId());
        return reloadTicket(body.ticketId());
    }

    @Transactional
    public Ticket assignRequest(long ticketId, UserInfo actor) {
        WorkflowTicketRow row = requireTicketRow(ticketId, actor);
        CompanyWorkflowConfig cfg = resolvePmConfig(row.pmCompanyId());
        if (!MaintenanceWorkflowRules.canAssign(cfg, row.estimatedAmount(), row.approvalStatus(), row.paymentStatus())) {
            throw new MaintenanceWorkflowException("Workflow prerequisites not satisfied for assignment");
        }
        if (row.status() != TicketStatus.READY_TO_ASSIGN && row.status() != TicketStatus.MANUAL_ASSIGNMENT) {
            throw new InvalidWorkflowStateException("Ticket must be READY_TO_ASSIGN (or MANUAL_ASSIGNMENT) to assign");
        }
        boolean ok = ticketService.assignSinglePendingTicket(ticketId);
        if (!ok) {
            throw new MaintenanceWorkflowException("Assignment did not succeed (no maintainer available or retries exhausted)");
        }
        return reloadTicket(ticketId);
    }

    @Transactional(readOnly = true)
    public MaintenanceWorkflowResponse getWorkflowDetails(long ticketId, UserInfo actor) {
        WorkflowTicketRow row = requireTicketRow(ticketId, actor);
        CompanyWorkflowConfig cfg = resolvePmConfig(row.pmCompanyId());
        Ticket t = ticketService.findById(ticketId).orElseThrow();
        boolean canAssign = MaintenanceWorkflowRules.canAssign(cfg, row.estimatedAmount(), row.approvalStatus(), row.paymentStatus());
        return MaintenanceWorkflowResponse.from(t, cfg, canAssign);
    }

    private Ticket reloadTicket(long ticketId) {
        return ticketService.findById(ticketId).orElseThrow();
    }
}
