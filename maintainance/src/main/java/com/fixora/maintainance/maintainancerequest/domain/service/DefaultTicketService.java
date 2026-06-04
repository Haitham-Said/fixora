package com.fixora.maintainance.maintainancerequest.domain.service;

import com.fixora.maintainance.maintainancerequest.application.visibility.TicketVisibilityService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import com.fixora.maintainance.maintainancerequest.domain.repository.ITicketRepository;
import com.fixora.maintainance.user.domain.repositories.IUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultTicketService implements TicketService{

    private final ITicketRepository ticketRepository;
    private final IUserRepository userRepository;

    public DefaultTicketService(ITicketRepository ticketRepository, IUserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public Page<Ticket> loadPortalTickets(TicketQuery ticketQuery, TicketVisibilityService.PortalTicketScope scope) {
        return ticketRepository.loadPortalTickets(ticketQuery, scope);
    }

    @Override
    public Ticket createNewTicket(TicketRequest ticketRequest, TicketRoutingDecision routing) {
        return ticketRepository.createNewTicket(ticketRequest, routing);
    }

    public void assignPendingTickets(){
         ticketRepository.assignUnassignedPendingTickets();
    }

    @Override
    public boolean assignSinglePendingTicket(Long ticketId) {
        return ticketRepository.assignSinglePendingTicket(ticketId);
    }

    @Override
    public java.util.Optional<Ticket> findById(Long ticketId) {
        return ticketRepository.findById(ticketId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public java.util.Optional<Ticket> findByIdReloaded(Long ticketId) {
        return ticketRepository.findById(ticketId);
    }

    @Override
    public java.util.List<Ticket> findMaintainerTicketsByStatus(Long maintainerUserId, TicketStatus status) {
        return ticketRepository.findMaintainerTicketsByStatus(maintainerUserId, status);
    }

    @Override
    public java.util.Optional<Ticket> findMaintainerTicketById(Long maintainerUserId, Long ticketId) {
        return ticketRepository.findMaintainerTicketById(maintainerUserId, ticketId);
    }

    @Override
    public Ticket startTicketForMaintainer(Long ticketId, Long maintainerUserId) {
        return ticketRepository.startTicketForMaintainer(ticketId, maintainerUserId);
    }

    @Override
    public Ticket completeTicketForMaintainer(Long ticketId, Long maintainerUserId) {
        return ticketRepository.completeTicketForMaintainer(ticketId, maintainerUserId);
    }

}
