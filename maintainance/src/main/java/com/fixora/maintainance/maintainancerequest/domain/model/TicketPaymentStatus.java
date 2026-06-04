package com.fixora.maintainance.maintainancerequest.domain.model;

/** Payment lifecycle on the ticket aggregate (gateway callback updates this; not JWT-secured). */
public enum TicketPaymentStatus {
    NOT_REQUIRED,
    PENDING,
    PAID,
    FAILED,
    COMPANY_BILLED
}
