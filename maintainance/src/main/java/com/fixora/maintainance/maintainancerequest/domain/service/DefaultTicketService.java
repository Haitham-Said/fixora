package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class DefaultTicketService implements TicketService{

    private final ITicketRepository ticketRepository;

    public DefaultTicketService(ITicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public Page<Ticket> loadCustomerTickets(TicketQuery ticketQuery){
        ticketRepository.loadCustomerTickets(ticketQuery);
}
