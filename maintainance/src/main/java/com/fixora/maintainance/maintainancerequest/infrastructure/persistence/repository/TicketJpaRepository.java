package com.fixora.maintainance.maintainancerequest.infrastructure.persistence.repository;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketJpaRepository extends JpaRepository<Ticket,Long> {

}
