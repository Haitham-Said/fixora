package com.fixora.maintainance.maintainancerequest.inbound.controller;

import com.fixora.maintainance.maintainancerequest.application.service.CustomerApplicationService;
import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.inbound.model.TicketQueryRequest;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import com.fixora.security.application.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("api/customer/tickets")
@PreAuthorize("hasAuthority('CUSTOMER')")
public class CustomerTicketController {

    @Autowired
    private CustomerApplicationService customerApplicationService;
    private final Logger logger= LoggerFactory.getLogger(CustomerTicketController.class);

    @GetMapping()
    public Page<Ticket> getTickets(@AuthenticationPrincipal UserInfo userInfo,
                                   @RequestParam("status") String status,
                                   @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
                                   @RequestParam("dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
                                   Pageable pageable){

        UUID requestId=UUID.randomUUID();
        logger.info("ticket query request received | userId :: {} , requestId :: {}",userInfo.userId(),requestId);

        TicketQueryRequest ticketQueryRequest=new TicketQueryRequest(requestId,TicketStatus.valueOf(status),dateFrom,dateTo,userInfo.companyId(),userInfo.role());

        return customerApplicationService.loadTickets(ticketQueryRequest,userInfo,pageable);

    }
}
