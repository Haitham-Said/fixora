package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

public class TicketRepository implements ITicketRepository {

    @Autowired
    private TicketJpaRepository ticketJpaRepository;

    @PersistenceContext
    private EntityManager entityManager;
    @Override

    public Page<Ticket> loadCustomerTickets(TicketQuery ticketQuery) {
        CriteriaBuilder criteriaBuilder=entityManager.getCriteriaBuilder();
        criteriaBuilder.createQuery(Ticket.class);

        return null;
    }
}
