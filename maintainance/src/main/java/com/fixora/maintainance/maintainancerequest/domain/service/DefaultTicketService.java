package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DefaultTicketService implements TicketService{

    private final TicketRepository ticketRepository;

    public DefaultTicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Page<Ticket> loadTickets(TicketQuery ticketQuery){
        return ticketRepository.loadTickets(ticketQuery);
    }
}
