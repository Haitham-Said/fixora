package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import org.springframework.data.domain.Page;

public interface TicketService {

    Page<Ticket> loadTickets(TicketQuery ticketQuery);
    Ticket createNewTicket(TicketRequest ticketRequest);
    void assignPendingTickets();
    Ticket updateTicketStatus(Long ticketId, TicketStatus newStatus, Long maintainerId);

}
