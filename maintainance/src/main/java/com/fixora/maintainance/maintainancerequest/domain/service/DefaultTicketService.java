package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class DefaultTicketService implements TicketService{

    private final ITicketRepository ticketRepository;
    private final IUserRepository userRepository;

    public DefaultTicketService(ITicketRepository ticketRepository, IUserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Page<Ticket> loadTickets(TicketQuery ticketQuery) {
        return ticketRepository.loadCustomerTickets(ticketQuery);
    }

    @Override
    public Ticket createNewTicket(TicketRequest ticketRequest) {
        return ticketRepository.createNewTicket(ticketRequest);
    }

    public void assignPendingTickets(){
         ticketRepository.assignUnassignedPendingTickets();
    }

}
