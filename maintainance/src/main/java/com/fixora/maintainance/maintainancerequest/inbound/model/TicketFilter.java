package com.fixora.maintainance.maintainancerequest.inbound.model;

import java.time.LocalDate;

public record TicketFilter(TicketStatus status, LocalDate dateFrom,LocalDate dateTo) {
}
