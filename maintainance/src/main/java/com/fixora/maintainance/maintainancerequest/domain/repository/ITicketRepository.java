package com.fixora.maintainance.maintainancerequest.domain.repository;


import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import org.springframework.data.domain.Page;

public interface ITicketRepository {

     Page<Ticket> loadCustomerTickets(TicketQuery ticketQuery);

}
