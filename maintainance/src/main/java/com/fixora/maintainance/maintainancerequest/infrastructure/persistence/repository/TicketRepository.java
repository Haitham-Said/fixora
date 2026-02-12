package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.*;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.TicketAssignmentWorker;
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

import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketRepository implements ITicketRepository {


    private final TicketJpaRepository ticketJpaRepository;
    private final TicketAssignmentWorker ticketAssignmentWorker;

    @PersistenceContext
    private EntityManager entityManager;

    public TicketRepository(TicketJpaRepository ticketJpaRepository, TicketAssignmentWorker ticketAssignmentWorker) {
        this.ticketJpaRepository = ticketJpaRepository;
        this.ticketAssignmentWorker = ticketAssignmentWorker;
    }

    @Override

    public Page<Ticket> loadCustomerTickets(TicketQuery ticketQuery) {
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        CriteriaQuery<MaintainanceRequest> criteriaQuery=criteriaBuilder.createQuery(MaintainanceRequest.class);
        Root<MaintainanceRequest> root=criteriaQuery.from(MaintainanceRequest.class);
        List<Predicate> predicates=new ArrayList<>();

        predicates.add(criteriaBuilder.equal(root.get("customer").get("user").get("id"), ticketQuery.getUserId()));
        if(ticketQuery.getFilter().getTicketStatus()!=null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), ticketQuery.getFilter().getTicketStatus()));
        }
        if(ticketQuery.getFilter().getDateFrom()!=null){
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"),ticketQuery.getFilter().getDateFrom()));
        }
        if(ticketQuery.getFilter().getDateTo()!=null){
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"),ticketQuery.getFilter().getDateTo()));
        }
        if(ticketQuery.getFilter().getCompanyId()!=null){
            predicates.add(criteriaBuilder.equal(root.get("company").get("id"),ticketQuery.getFilter().getCompanyId()));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        TypedQuery<MaintainanceRequest> query= entityManager.createQuery(criteriaQuery);
        
        // Use domain pagination
        var pagination = ticketQuery.getPagination() != null 
            ? ticketQuery.getPagination() 
            : com.fixora.maintainance.maintainancerequest.domain.model.PaginationRequest.builder().build();
        
        query.setFirstResult(pagination.getOffset());
        query.setMaxResults(pagination.getPageSize());
        List<MaintainanceRequest> result=query.getResultList();

         List<Ticket> tickets=result.stream()
                .map(TicketMapper::toTicket)
                .toList();
         
         // Convert domain pagination to Spring Pageable for PageImpl
         org.springframework.data.domain.Pageable pageable = 
             org.springframework.data.domain.PageRequest.of(
                 pagination.getPageNumber(), 
                 pagination.getPageSize()
             );
         return new PageImpl<>(tickets, pageable, getTotalCount(ticketQuery));
    }

    private long getTotalCount(TicketQuery ticketQuery) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<MaintainanceRequest> root = countQuery.from(MaintainanceRequest.class);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.equal(root.get("customer").get("user").get("id"), ticketQuery.getUserId()));

        if (ticketQuery.getFilter().getTicketStatus() != null) {
            predicates.add(cb.equal(root.get("status"), ticketQuery.getFilter().getTicketStatus()));
        }
        if (ticketQuery.getFilter().getDateFrom() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), ticketQuery.getFilter().getDateFrom()));
        }
        if (ticketQuery.getFilter().getDateTo() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), ticketQuery.getFilter().getDateTo()));
        }
        if (ticketQuery.getFilter().getCompanyId() != null) {
            predicates.add(cb.equal(root.get("company").get("id"), ticketQuery.getFilter().getCompanyId()));
        }

        countQuery.select(cb.count(root));
        countQuery.where(predicates.toArray(new Predicate[0]));

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    @Transactional
    public Ticket createNewTicket(TicketRequest ticketRequest){
        MaintainanceRequest maintainanceRequest=new MaintainanceRequest();
        maintainanceRequest.setDescription(ticketRequest.getDescription());
        maintainanceRequest.setPictureUrl(ticketRequest.getImageUrl());
        maintainanceRequest.setStatus(TicketStatus.PENDING);
        Customer customer = entityManager.find(Customer.class, ticketRequest.getUserId());
        Company company=entityManager.find(Company.class,ticketRequest.getCompanyId());
        Apartment apartment=customer.getApartment();
        Building building=apartment.getBuilding();
        maintainanceRequest.setApartment(apartment);
        maintainanceRequest.setCustomer(customer);
        maintainanceRequest.setBuilding(building);
        maintainanceRequest.setCompany(company);
        maintainanceRequest.setPreferredTime(ticketRequest.getPreferredSlot().toString());

        entityManager.persist(maintainanceRequest);

        return TicketMapper.toTicket(maintainanceRequest);

    }

    @Transactional
    public void assignUnassignedPendingTickets(){

         ticketJpaRepository.findUnassignedPendingTickets().
                forEach(ticketAssignmentWorker::assignSingleTicketSafely);

    }

    @Override
    @Transactional
    public Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus, Long maintainerId) {
        MaintainanceRequest request = entityManager.find(MaintainanceRequest.class, ticketId);
        if (request == null) {
            throw new IllegalArgumentException("Ticket not found with ID: " + ticketId);
        }
        
        // Verify the maintainer is assigned to this ticket
        if (request.getMaintainer() == null || !request.getMaintainer().getUserId().equals(maintainerId)) {
            throw new IllegalArgumentException("Maintainer is not assigned to this ticket");
        }
        
        // Validate status transition (only allow CLOSED or FIXED from ASSIGNED/IN_PROGRESS)
        TicketStatus currentStatus = request.getStatus();
        if (currentStatus != TicketStatus.ASSIGNED && currentStatus != TicketStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Cannot update status from " + currentStatus + ". Ticket must be ASSIGNED or IN_PROGRESS.");
        }
        
        if (newStatus != TicketStatus.CLOSED && newStatus != TicketStatus.FIXED) {
            throw new IllegalArgumentException("Maintainers can only set status to CLOSED or FIXED");
        }
        
        request.setStatus(newStatus);
        request.setUpdatedAt(java.time.LocalDateTime.now());
        entityManager.merge(request);
        
        return TicketMapper.toTicket(request);
    }

}
