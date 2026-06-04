package com.fixora.maintainance.maintainancerequest.domain.repository;


import com.fixora.maintainance.maintainancerequest.application.visibility.TicketVisibilityService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketQuery;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.routing.TicketRoutingDecision;
import org.springframework.data.domain.Page;

public interface ITicketRepository {

     Page<Ticket> loadPortalTickets(TicketQuery ticketQuery, TicketVisibilityService.PortalTicketScope scope);

     Ticket createNewTicket(TicketRequest ticketRequest, TicketRoutingDecision routing);

     void assignUnassignedPendingTickets();

     boolean assignSinglePendingTicket(Long ticketId);

     java.util.Optional<Ticket> findById(Long ticketId);

     java.util.List<Ticket> findMaintainerTicketsByStatus(Long maintainerUserId, TicketStatus status);

     java.util.Optional<Ticket> findMaintainerTicketById(Long maintainerUserId, Long ticketId);

     Ticket startTicketForMaintainer(Long ticketId, Long maintainerUserId);

     Ticket completeTicketForMaintainer(Long ticketId, Long maintainerUserId);

}
