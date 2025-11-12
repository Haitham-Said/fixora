package com.fixora.maintainance.maintainancerequest.application.job;

import com.fixora.maintainance.maintainancerequest.application.service.TicketAssignmentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TicketAssignmentJob {

    private final TicketAssignmentService ticketAssignmentService;

    public TicketAssignmentJob(TicketAssignmentService ticketAssignmentService) {
        this.ticketAssignmentService = ticketAssignmentService;
    }

    @Scheduled(fixedRate = 3*60*1000)
    public void ticketAssignment() {
        ticketAssignmentService.assignPendingTickets();
    }
}
