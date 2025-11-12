package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.CustomerTicketApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.model.Urgency;
import com.fixora.maintainance.maintainancerequest.inbound.model.PreferredSlot;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.security.application.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/customer/tickets")
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerTicketController {


    private final CustomerTicketApplicationService customerTicketApplicationService;
    private final Logger logger= LoggerFactory.getLogger(CustomerTicketController.class);

    public CustomerTicketController(CustomerTicketApplicationService customerTicketApplicationService) {
        this.customerTicketApplicationService = customerTicketApplicationService;
    }

    @GetMapping()
    public Page<Ticket> getTickets(@AuthenticationPrincipal UserInfo userInfo,
                                   @RequestParam(value = "status",required = false) String status,
                                   @RequestParam(value = "dateFrom",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                   @RequestParam(value = "dateTo",required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                   Pageable pageable){

        UUID requestId=UUID.randomUUID();
        logger.info("ticket query request received | userId :: {} , requestId :: {}",userInfo.userId(),requestId);

        TicketQueryRequest ticketQueryRequest=new TicketQueryRequest(requestId,TicketStatus.valueOf(status),dateFrom,dateTo,userInfo.companyId(),userInfo.role());

        return customerTicketApplicationService.loadTickets(ticketQueryRequest,userInfo,pageable);

    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Ticket createTicket(@AuthenticationPrincipal UserInfo userInfo,
                               @RequestParam("description") String description,
                               @RequestParam(value = "preferredSlot", required = false) PreferredSlot preferredSlot,
                               @RequestParam(value = "urgency", required = false) Urgency urgency,
                               @RequestParam(value = "image", required = false) MultipartFile image)
    {
        UUID requestId=UUID.randomUUID();
        return customerTicketApplicationService.createTicket(requestId,userInfo,description,preferredSlot,urgency,image);

    }


}
