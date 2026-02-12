package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.MaintainerTicketApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.security.application.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/maintainer/tickets")
@PreAuthorize("hasAuthority('MAINTAINER')")
public class MaintainerTicketController {

    private final MaintainerTicketApplicationService maintainerTicketApplicationService;
    private final Logger logger= LoggerFactory.getLogger(MaintainerTicketController.class);

    public MaintainerTicketController(MaintainerTicketApplicationService maintainerTicketApplicationService) {
        this.maintainerTicketApplicationService = maintainerTicketApplicationService;
    }

    @GetMapping()
    public Page<Ticket> getTickets(@AuthenticationPrincipal UserInfo userInfo,
                                   @RequestParam(value = "status",required = false) String status,
                                   @RequestParam(value = "dateFrom",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                   @RequestParam(value = "dateTo",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                   Pageable pageable){

        UUID requestId=UUID.randomUUID();
        logger.info("ticket query request received | userId :: {} , requestId :: {}",userInfo.userId(),requestId);

        TicketQueryRequest ticketQueryRequest=new TicketQueryRequest(requestId, TicketStatus.valueOf(status),dateFrom,dateTo,userInfo.companyId(),userInfo.role());

        return maintainerTicketApplicationService.loadTickets(ticketQueryRequest,userInfo,pageable);

    }

    @PutMapping("/{ticketId}/status")
    public Ticket updateTicketStatus(@PathVariable Long ticketId,
                                     @RequestBody Map<String, String> statusRequest,
                                     @AuthenticationPrincipal UserInfo userInfo) {
        String statusStr = statusRequest.get("status");
        if (statusStr == null) {
            throw new IllegalArgumentException("Status is required");
        }
        TicketStatus newStatus = TicketStatus.valueOf(statusStr.toUpperCase());
        return maintainerTicketApplicationService.updateTicketStatus(ticketId, newStatus, userInfo);
    }

}
