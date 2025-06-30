package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.maintainancerequest.infrastructure.persistence.entity.MaintainanceRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class TicketRepository implements ITicketRepository {


    private final TicketJpaRepository ticketJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public TicketRepository(TicketJpaRepository ticketJpaRepository) {
        this.ticketJpaRepository = ticketJpaRepository;
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
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"),ticketQuery.getFilter().getDateFrom()));
        }
        if(ticketQuery.getFilter().getCompanyId()!=null){
            predicates.add(criteriaBuilder.equal(root.get("company").get("id"),ticketQuery.getFilter().getCompanyId()));
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        TypedQuery<MaintainanceRequest> query= entityManager.createQuery(criteriaQuery);
        query.setFirstResult((int) ticketQuery.getPageable().getOffset());
        query.setMaxResults(ticketQuery.getPageable().getPageSize());
        List<MaintainanceRequest> result=query.getResultList();

        List<Ticket> ticketList = result.stream()
                .map(this::mapToTicket) // stub for now
                .toList();
        return null;
    }
}
