package com.fixora.maintainance.maintainancerequest.application.service;

import com.fixora.maintainance.maintainancerequest.domain.model.Ticket;
import com.fixora.maintainance.maintainancerequest.domain.service.TicketService;
import com.fixora.maintainance.user.domain.model.Maintainer;
import com.fixora.maintainance.user.domain.service.UserService;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TicketAssignmentService {
    private final TicketService ticketService;
    private final UserService userService;

    public TicketAssignmentService(TicketService ticketService, UserService userService) {
        this.ticketService = ticketService;
        this.userService = userService;
    }

    public void assignPendingTickets(){

        ticketService.assignPendingTickets();


    }
}
