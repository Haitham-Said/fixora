package com.fixora.maintainance.maintainancerequest.domain.model.requests;

import com.fixora.maintainance.maintainancerequest.domain.model.PaginationRequest;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class TicketQuery {
    private UUID requestId;
    private Long userId;
    private TicketFilter filter;
    private PaginationRequest pagination;
}

