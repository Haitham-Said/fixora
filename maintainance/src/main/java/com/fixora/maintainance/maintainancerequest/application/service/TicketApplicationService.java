package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.mapper.TicketMapper;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.security.application.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TicketApplicationService {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    public TicketApplicationService(TicketService ticketService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
    }

    public Page<Ticket> loadTickets(TicketQueryRequest ticketQueryRequest, UserInfo userInfo, Pageable pageable){
        return ticketService.loadTickets(ticketMapper.toDomain(ticketQueryRequest,userInfo,pageable));
    }

}
