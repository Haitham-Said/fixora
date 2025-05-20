package com.fixora.maintainance.maintainancerequest.domain.model;

import com.fixora.maintainance.maintainancerequest.inbound.model.TicketFilter;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Builder
public class TicketQuery {
    private UUID requestId;
    private Long userId;
    private TicketFilter filters;
    private Pageable pageable;
}

