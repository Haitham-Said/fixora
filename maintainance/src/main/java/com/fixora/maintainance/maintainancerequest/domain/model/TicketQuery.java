package com.fixora.maintainance.maintainancerequest.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


@Data
@Builder
public class TicketQuery {
    private UUID requestId;
    private Long userId;
    private TicketFilter filter;
    private Pageable pageable;
}

