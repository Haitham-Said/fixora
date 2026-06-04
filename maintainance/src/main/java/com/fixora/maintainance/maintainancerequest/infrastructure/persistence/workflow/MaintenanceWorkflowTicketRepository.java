package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.workflow;

import com.fixora.maintainance.maintainancerequest.domain.exception.MaintenanceWorkflowException;
import com.fixora.maintainance.maintainancerequest.domain.exception.WorkflowAccessDeniedException;
import com.fixora.maintainance.maintainancerequest.domain.model.MaintenanceWorkflowRules;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketApprovalStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.workflow.WorkflowTicketRow;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class MaintenanceWorkflowTicketRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<WorkflowTicketRow> loadRow(long ticketId) {
        return Optional.ofNullable(entityManager.find(MaintainanceRequest.class, ticketId))
                .map(WorkflowTicketRowMapper::toRow);
    }

    public Optional<WorkflowTicketRow> loadRowForPmCompany(long ticketId, long pmCompanyId) {
        return findByIdAndPmCompany(ticketId, pmCompanyId).map(WorkflowTicketRowMapper::toRow);
    }

    public Optional<WorkflowTicketRow> loadRowForExecutorCompany(long ticketId, long executorCompanyId) {
        return findByIdAndExecutorCompany(ticketId, executorCompanyId).map(WorkflowTicketRowMapper::toRow);
    }

    public Optional<WorkflowTicketRow> loadRowForPortalActor(long ticketId, Long userCompanyId, String role) {
        if (userCompanyId == null) {
            return Optional.empty();
        }
        if ("OPERATION".equalsIgnoreCase(role)) {
            return loadRow(ticketId);
        }
        if ("FM_ADMIN".equalsIgnoreCase(role)) {
            return entityManager.createQuery("""
                            SELECT t FROM MaintainanceRequest t
                            WHERE t.id = :id
                              AND (t.executorCompany.id = :cid OR t.facilityManagementCompany.id = :cid)
                            """, MaintainanceRequest.class)
                    .setParameter("id", ticketId)
                    .setParameter("cid", userCompanyId)
                    .getResultStream()
                    .findFirst()
                    .map(WorkflowTicketRowMapper::toRow);
        }
        return loadRowForPmCompany(ticketId, userCompanyId);
    }

    private Optional<MaintainanceRequest> findByIdAndPmCompany(long ticketId, long pmCompanyId) {
        return entityManager.createQuery("""
                        SELECT t FROM MaintainanceRequest t
                        WHERE t.id = :id AND t.pmCompany.id = :pmCompanyId
                        """, MaintainanceRequest.class)
                .setParameter("id", ticketId)
                .setParameter("pmCompanyId", pmCompanyId)
                .getResultStream()
                .findFirst();
    }

    private Optional<MaintainanceRequest> findByIdAndExecutorCompany(long ticketId, long executorCompanyId) {
        return entityManager.createQuery("""
                        SELECT t FROM MaintainanceRequest t
                        WHERE t.id = :id AND t.executorCompany.id = :executorCompanyId
                        """, MaintainanceRequest.class)
                .setParameter("id", ticketId)
                .setParameter("executorCompanyId", executorCompanyId)
                .getResultStream()
                .findFirst();
    }

    private MaintainanceRequest requireById(long ticketId) {
        MaintainanceRequest r = entityManager.find(MaintainanceRequest.class, ticketId);
        if (r == null) {
            throw new MaintenanceWorkflowException("Ticket not found");
        }
        return r;
    }

    @Transactional
    public void applyEstimation(long ticketId, long executorCompanyId, BigDecimal amount, String note,
                                MaintenanceWorkflowRules.EstimationOutcome outcome) {
        MaintainanceRequest r = findByIdAndExecutorCompany(ticketId, executorCompanyId)
                .orElseThrow(() -> new WorkflowAccessDeniedException("Ticket not found for executor company"));
        r.setEstimatedAmount(amount);
        r.setEstimationNote(note);
        r.setStatus(outcome.ticketStatus());
        r.setTicketApprovalStatus(outcome.approvalStatus());
        r.setTicketPaymentStatus(outcome.paymentStatus());
        r.setPayerType(outcome.payerType());
        syncLegacyApprovalPaidFlags(r);
        r.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(r);
    }

    @Transactional
    public void applyApproval(long ticketId, long pmCompanyId, Long approvedByUserId,
                              MaintenanceWorkflowRules.ApprovalOutcome outcome) {
        MaintainanceRequest r = findByIdAndPmCompany(ticketId, pmCompanyId)
                .orElseThrow(() -> new WorkflowAccessDeniedException("Ticket not found for PM company"));
        r.setApproved(true);
        r.setTicketApprovalStatus(TicketApprovalStatus.APPROVED);
        r.setApprovedBy(approvedByUserId);
        r.setApprovedAt(LocalDateTime.now());
        r.setApprovalActor("PROPERTY_ADMIN");
        r.setStatus(outcome.ticketStatus());
        r.setTicketPaymentStatus(outcome.paymentStatus());
        r.setPayerType(outcome.payerType());
        syncLegacyApprovalPaidFlags(r);
        r.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(r);
    }

    @Transactional
    public void applyRejection(long ticketId, long pmCompanyId) {
        MaintainanceRequest r = findByIdAndPmCompany(ticketId, pmCompanyId)
                .orElseThrow(() -> new WorkflowAccessDeniedException("Ticket not found for PM company"));
        r.setStatus(TicketStatus.REJECTED);
        r.setTicketApprovalStatus(TicketApprovalStatus.REJECTED);
        r.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(r);
    }

    @Transactional
    public void applyPaid(long ticketId, String paymentRef, TicketStatus nextStatus) {
        MaintainanceRequest r = requireById(ticketId);
        r.setPaid(true);
        r.setPaymentRef(paymentRef);
        r.setTicketPaymentStatus(TicketPaymentStatus.PAID);
        r.setStatus(nextStatus);
        r.setUpdatedAt(LocalDateTime.now());
        entityManager.merge(r);
    }

    private static void syncLegacyApprovalPaidFlags(MaintainanceRequest r) {
        r.setApproved(r.getTicketApprovalStatus() == TicketApprovalStatus.APPROVED);
        TicketPaymentStatus ps = r.getTicketPaymentStatus();
        r.setPaid(ps == TicketPaymentStatus.PAID || ps == TicketPaymentStatus.COMPANY_BILLED);
    }
}
