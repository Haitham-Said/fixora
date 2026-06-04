package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.application.assignment.MaintainerAssignmentService;
import com.fixora.maintainance.maintainancerequest.application.visibility.TicketVisibilityService;
import com.fixora.maintainance.maintainancerequest.domain.model.MaintenanceWorkflowRules;
import com.fixora.maintainance.maintainancerequest.domain.model.PortalTicketQueue;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketApprovalStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentPayerType;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketPaymentStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.mapper.TicketMapper;
import com.fixora.maintainance.user.infrastructure.entity.customer.Customer;
import com.fixora.maintainance.property.infrastructure.entity.Apartment;
import com.fixora.maintainance.property.infrastructure.entity.Building;
import com.fixora.maintainance.property.infrastructure.entity.Company;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TicketRepository implements ITicketRepository {


    private final TicketJpaRepository ticketJpaRepository;
    private final MaintainerAssignmentService maintainerAssignmentService;

    @PersistenceContext
    private EntityManager entityManager;

    public TicketRepository(TicketJpaRepository ticketJpaRepository,
                            MaintainerAssignmentService maintainerAssignmentService) {
        this.ticketJpaRepository = ticketJpaRepository;
        this.maintainerAssignmentService = maintainerAssignmentService;
    }

    @Override
    public Page<Ticket> loadPortalTickets(TicketQuery ticketQuery, TicketVisibilityService.PortalTicketScope scope) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<MaintainanceRequest> cq = cb.createQuery(MaintainanceRequest.class);
        Root<MaintainanceRequest> root = cq.from(MaintainanceRequest.class);
        List<Predicate> predicates = buildPortalPredicates(cb, root, ticketQuery, scope);
        cq.where(predicates.toArray(new Predicate[0]));
        cq.orderBy(cb.desc(root.get("updatedAt")));
        TypedQuery<MaintainanceRequest> query = entityManager.createQuery(cq);

        var pagination = ticketQuery.getPagination() != null
                ? ticketQuery.getPagination()
                : com.fixora.maintainance.maintainancerequest.domain.model.PaginationRequest.builder().build();
        query.setFirstResult(pagination.getOffset());
        query.setMaxResults(pagination.getPageSize());
        List<Ticket> tickets = query.getResultList().stream().map(TicketMapper::toTicket).toList();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                pagination.getPageNumber(), pagination.getPageSize());
        return new PageImpl<>(tickets, pageable, countPortalTickets(ticketQuery, scope));
    }

    private List<Predicate> buildPortalPredicates(
            CriteriaBuilder cb, Root<MaintainanceRequest> root, TicketQuery ticketQuery,
            TicketVisibilityService.PortalTicketScope scope) {
        List<Predicate> predicates = new ArrayList<>();
        PortalTicketQueue queue = ticketQuery.getFilter().getQueue();
        if (queue == PortalTicketQueue.NEEDS_ESTIMATION) {
            predicates.add(cb.equal(root.get("status"), TicketStatus.NEEDS_ESTIMATION));
            if (!scope.operationAll()) {
                Long companyId = ticketQuery.getFilter().getCompanyId();
                if (companyId != null) {
                    predicates.add(cb.equal(root.get("executorCompany").get("id"), companyId));
                }
                MaintenanceWorkflowRules.estimationActorForPortalRole(ticketQuery.getFilter().getRole())
                        .ifPresent(actor -> predicates.add(cb.equal(root.get("ticketEstimationActor"), actor)));
            }
        } else {
            if (!scope.operationAll()) {
                if (scope.pmCompanyId() != null) {
                    predicates.add(cb.equal(root.get("pmCompany").get("id"), scope.pmCompanyId()));
                } else if (scope.fmCompanyId() != null) {
                    Predicate exec = cb.equal(root.get("executorCompany").get("id"), scope.fmCompanyId());
                    Predicate fm = cb.equal(root.get("facilityManagementCompany").get("id"), scope.fmCompanyId());
                    predicates.add(cb.or(exec, fm));
                }
            }
            if (ticketQuery.getFilter().getTicketStatus() != null) {
                predicates.add(cb.equal(root.get("status"), ticketQuery.getFilter().getTicketStatus()));
            }
        }
        if (ticketQuery.getFilter().getDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("updatedAt"), ticketQuery.getFilter().getDateFrom()));
        }
        if (ticketQuery.getFilter().getDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("updatedAt"), ticketQuery.getFilter().getDateTo()));
        }
        return predicates;
    }

    private long countPortalTickets(TicketQuery ticketQuery, TicketVisibilityService.PortalTicketScope scope) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MaintainanceRequest> root = countQuery.from(MaintainanceRequest.class);
        countQuery.select(cb.count(root));
        countQuery.where(buildPortalPredicates(cb, root, ticketQuery, scope).toArray(new Predicate[0]));
        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Transactional
    public Ticket createNewTicket(TicketRequest ticketRequest, TicketRoutingDecision routing) {
        MaintainanceRequest maintainanceRequest = new MaintainanceRequest();
        maintainanceRequest.setDescription(ticketRequest.getDescription());
        maintainanceRequest.setPictureUrl(ticketRequest.getImageUrl());

        Customer customer = entityManager.find(Customer.class, ticketRequest.getUserId());
        Company pmCompany = entityManager.find(Company.class, routing.pmCompanyId());
        Company executorCompany = entityManager.find(Company.class, routing.executorCompanyId());
        Company fmCompany = routing.facilityManagementCompanyId() != null
                ? entityManager.find(Company.class, routing.facilityManagementCompanyId())
                : null;
        Apartment apartment = customer.getApartment();
        Building building = apartment.getBuilding();
        maintainanceRequest.setApartment(apartment);
        maintainanceRequest.setCustomer(customer);
        maintainanceRequest.setBuilding(building);
        maintainanceRequest.setPmCompany(pmCompany);
        maintainanceRequest.setExecutorCompany(executorCompany);
        maintainanceRequest.setFacilityManagementCompany(fmCompany);
        maintainanceRequest.setPreferredTime(ticketRequest.getPreferredSlot().toString());
        maintainanceRequest.setPreferredVisitDate(ticketRequest.getPreferredVisitDate());
        maintainanceRequest.setStatus(routing.initialStatus());
        maintainanceRequest.setTicketEstimationActor(routing.estimationActor());
        maintainanceRequest.setTicketApprovalActor(routing.approvalActor());
        maintainanceRequest.setApproved(false);
        maintainanceRequest.setPaid(false);
        maintainanceRequest.setTicketApprovalStatus(TicketApprovalStatus.NOT_REQUIRED);
        maintainanceRequest.setTicketPaymentStatus(TicketPaymentStatus.NOT_REQUIRED);
        maintainanceRequest.setPayerType(TicketPaymentPayerType.NONE);

        entityManager.persist(maintainanceRequest);
        return TicketMapper.toTicket(maintainanceRequest);
    }

    @Transactional
    public void assignUnassignedPendingTickets(){
         ticketJpaRepository.findUnassignedPendingTickets()
                .forEach(r -> attemptAssignmentAndTrackOutcome(r, true));

    }

    @Override
    @Transactional
    public boolean assignSinglePendingTicket(Long ticketId) {
        MaintainanceRequest request = entityManager.find(MaintainanceRequest.class, ticketId);
        if (request == null) {
            return false;
        }
        if (request.getMaintainer() != null) {
            return true;
        }
        if (request.getAssignmentRetryCount() != null && request.getAssignmentRetryCount() >= 3) {
            return false;
        }
        if (!isAssignableQueueStatus(request.getStatus())) {
            return false;
        }

        attemptAssignmentAndTrackOutcome(request, false);
        return request.getMaintainer() != null;
    }

    /** Only this status is picked up by the assignment scheduler / single-assign path. */
    private static boolean isAssignableQueueStatus(TicketStatus status) {
        return status == TicketStatus.READY_TO_ASSIGN;
    }

    /**
     * @param isolatedTransaction {@code true} for batch/scheduler (REQUIRES_NEW per ticket);
     *                            {@code false} when the row may still be uncommitted (e.g. immediate assign after create).
     */
    private void attemptAssignmentAndTrackOutcome(MaintainanceRequest request, boolean isolatedTransaction) {
        request.setLastAssignmentAttemptAt(LocalDateTime.now());
        boolean assigned = isolatedTransaction
                ? maintainerAssignmentService.assignSingleTicketSafely(request)
                : maintainerAssignmentService.assignSingleTicketInCurrentTransaction(request);
        // After REQUIRES_NEW the outer persistence context can be stale; same-tx assign already updates this instance.
        if (isolatedTransaction) {
            entityManager.refresh(request);
        }

        if (!assigned && request.getMaintainer() == null && isAssignableQueueStatus(request.getStatus())) {
            int current = request.getAssignmentRetryCount() == null ? 0 : request.getAssignmentRetryCount();
            int next = current + 1;
            request.setAssignmentRetryCount(next);
            if (next >= 3) {
                request.setStatus(TicketStatus.MANUAL_ASSIGNMENT);
            }
            request.setUpdatedAt(LocalDateTime.now());
            entityManager.merge(request);
        }
    }

    @Override
    public java.util.Optional<Ticket> findById(Long ticketId) {
        MaintainanceRequest entity = entityManager.find(MaintainanceRequest.class, ticketId);
        return entity != null ? java.util.Optional.of(TicketMapper.toTicket(entity)) : java.util.Optional.empty();
    }

    @Override
    public List<Ticket> findMaintainerTicketsByStatus(Long maintainerUserId, TicketStatus status) {
        return entityManager.createQuery("""
                SELECT t FROM MaintainanceRequest t
                WHERE t.maintainer IS NOT NULL
                  AND t.maintainer.userId = :maintainerUserId
                  AND t.status = :status
                ORDER BY t.updatedAt DESC
                """, MaintainanceRequest.class)
                .setParameter("maintainerUserId", maintainerUserId)
                .setParameter("status", status)
                .getResultList()
                .stream()
                .map(TicketMapper::toTicket)
                .toList();
    }

    @Override
    public Optional<Ticket> findMaintainerTicketById(Long maintainerUserId, Long ticketId) {
        MaintainanceRequest request = entityManager.find(MaintainanceRequest.class, ticketId);
        if (request == null || request.getMaintainer() == null || !maintainerUserId.equals(request.getMaintainer().getUserId())) {
            return Optional.empty();
        }
        return Optional.of(TicketMapper.toTicket(request));
    }

    @Override
    @Transactional
    public Ticket startTicketForMaintainer(Long ticketId, Long maintainerUserId) {
        MaintainanceRequest request = entityManager.find(MaintainanceRequest.class, ticketId);
        if (request == null) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }
        if (request.getMaintainer() == null || !maintainerUserId.equals(request.getMaintainer().getUserId())) {
            throw new IllegalArgumentException("Ticket is not assigned to this maintainer");
        }
        if (request.getStatus() != TicketStatus.ASSIGNED) {
            throw new IllegalArgumentException("Ticket is no longer open for Start");
        }

        request.setStatus(TicketStatus.IN_PROGRESS);
        request.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(request);
        return TicketMapper.toTicket(request);
    }

    @Override
    @Transactional
    public Ticket completeTicketForMaintainer(Long ticketId, Long maintainerUserId) {
        MaintainanceRequest request = entityManager.find(MaintainanceRequest.class, ticketId);
        if (request == null) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }
        if (request.getMaintainer() == null || !maintainerUserId.equals(request.getMaintainer().getUserId())) {
            throw new IllegalArgumentException("Ticket is not assigned to this maintainer");
        }
        if (request.getStatus() != TicketStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Ticket must be IN_PROGRESS before completion");
        }

        request.setStatus(TicketStatus.COMPLETED);
        request.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(request);
        return TicketMapper.toTicket(request);
    }

}
