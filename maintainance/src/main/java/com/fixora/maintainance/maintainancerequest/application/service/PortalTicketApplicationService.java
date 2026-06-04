package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.mapper.TicketMapper;
import com.fixora.maintainance.maintainancerequest.application.visibility.TicketVisibilityService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.security.application.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PortalTicketApplicationService {
    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final TicketVisibilityService ticketVisibilityService;

    public PortalTicketApplicationService(
            TicketService ticketService,
            TicketMapper ticketMapper,
            TicketVisibilityService ticketVisibilityService) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
        this.ticketVisibilityService = ticketVisibilityService;
    }

    public Page<Ticket> loadTickets(TicketQueryRequest ticketQueryRequest, UserInfo userInfo, Pageable pageable) {
        TicketVisibilityService.PortalTicketScope scope = ticketVisibilityService.scopeFor(userInfo);
        return ticketService.loadPortalTickets(ticketMapper.toDomain(ticketQueryRequest, userInfo, pageable), scope);
    }
}
