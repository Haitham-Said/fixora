package com.fixora.maintainance.maintainancerequest.domain.model.requests;

import com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class TicketFilter {
    private TicketStatus ticketStatus;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Long companyId;
    private String role;

}
