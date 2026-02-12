package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.application.mapper.TicketMapper;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.requests.TicketRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.Urgency;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.maintainancerequest.domain.model.PreferredSlot;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.maintainance.user.domain.service.IStorageService;
import com.fixora.security.application.model.UserInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class CustomerTicketApplicationService {

    private final TicketService ticketService;
    private final TicketMapper ticketMapper;
    private final IStorageService storageService;

    public CustomerTicketApplicationService(TicketService ticketService, TicketMapper ticketMapper,
                                           IStorageService storageService) {
        this.ticketService = ticketService;
        this.ticketMapper = ticketMapper;
        this.storageService = storageService;
    }

    public Page<Ticket> loadTickets(TicketQueryRequest ticketQueryRequest, UserInfo userInfo, Pageable pageable){

        return ticketService.loadTickets(ticketMapper.toDomain(ticketQueryRequest,userInfo,pageable));
    }

    public Ticket createTicket(UUID requestId, UserInfo userInfo, String description, PreferredSlot preferredSlot, Urgency urgency, MultipartFile image) {
        // Upload image to storage if provided
        String imageUrl = null;
        if (image != null && !image.isEmpty()) {
            try {
                imageUrl = storageService.uploadFile(
                    image.getOriginalFilename(),
                    image.getContentType(),
                    image.getBytes()
                );
            } catch (IOException e) {
                throw new RuntimeException("Error uploading image: " + e.getMessage(), e);
            }
        }
        
        return ticketService.createNewTicket(
                TicketRequest.builder().requestId(requestId)
                        .userId(userInfo.userId())
                        .companyId(userInfo.companyId())
                        .preferredSlot(preferredSlot)
                        .userEmail(userInfo.userEmail())
                        .description(description)
                        .urgency(urgency)
                        .imageUrl(imageUrl)
                        .build()
        );
    }
}
