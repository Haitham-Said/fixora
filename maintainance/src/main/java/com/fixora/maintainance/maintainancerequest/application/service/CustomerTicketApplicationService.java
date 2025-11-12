package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.mapper.TicketMapper;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.Urgency;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.maintainancerequest.inbound.model.PreferredSlot;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.security.application.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class CustomerTicketApplicationService {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;

    public CustomerTicketApplicationService(TicketService ticketService, TicketMapper ticketMapper) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
    }

    public Page<Ticket> loadTickets(TicketQueryRequest ticketQueryRequest, UserInfo userInfo, Pageable pageable){

        return ticketService.loadTickets(ticketMapper.toDomain(ticketQueryRequest,userInfo,pageable));
    }

    public Ticket createTicket(UUID requestId, UserInfo userInfo, String description, PreferredSlot preferredSlot, Urgency urgency, MultipartFile image) {
        return ticketService.createNewTicket(
                TicketRequest.builder().requestId(requestId)
                        .userId(userInfo.userId())
                        .companyId(userInfo.companyId())
                        .preferredSlot(preferredSlot)
                        .userEmail(userInfo.userEmail())
                        .description(description)
                        .urgency(urgency)
                        .imageUrl("http://pathtoimage.com")
                        .build()
        );
    }
}
