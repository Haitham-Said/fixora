package com.fixora.maintainance.maintainancerequest.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TicketFilter {
    private TicketStatus ticketStatus;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer companyId;
    private String role;

}
