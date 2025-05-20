package com.fixora.maintainance.maintainancerequest.domain.repository;


import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import org.springframework.data.domain.Page;

public interface TicketRepository {

     Page<Ticket> loadTickets(TicketQuery ticketQuery);

}
