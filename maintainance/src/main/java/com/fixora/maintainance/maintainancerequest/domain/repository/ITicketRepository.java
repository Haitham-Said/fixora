package com.fixora.maintainance.maintainancerequest.domain.repository;


import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import org.springframework.data.domain.Page;

public interface ITicketRepository {

     Page<Ticket> loadCustomerTickets(TicketQuery ticketQuery);

     Ticket createNewTicket(TicketRequest ticketRequest);

     void assignUnassignedPendingTickets();


}
