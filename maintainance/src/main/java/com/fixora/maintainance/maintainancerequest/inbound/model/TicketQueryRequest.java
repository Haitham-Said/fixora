package com.fixora.maintainance.maintainancerequest.inbound.model;

import java.util.UUID;

public record TicketQueryRequest(
     UUID requestId,
     TicketFilter filters
){}
