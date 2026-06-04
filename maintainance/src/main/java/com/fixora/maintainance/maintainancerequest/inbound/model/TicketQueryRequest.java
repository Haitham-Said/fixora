package com.fixora.maintainance.maintainancerequest.inbound.model;

import com.fixora.maintainance.maintainancerequest.domain.model.PortalTicketQueue;
import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;

import java.time.LocalDate;
import java.util.UUID;

public record TicketQueryRequest(
     UUID requestId,
     TicketStatus ticketStatus,
     LocalDate dateFrom,
     LocalDate dateTo,
     Long companyId,
     String role,
     PortalTicketQueue queue
){}
