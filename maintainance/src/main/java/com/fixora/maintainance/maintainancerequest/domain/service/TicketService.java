package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import org.springframework.data.domain.Page;

public interface TicketService {

    Page<Ticket> loadTickets(TicketQuery ticketQuery);
}
