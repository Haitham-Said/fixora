package com.fixora.maintainance.maintainancerequest.domain.model;

/** Who is expected to fund the maintenance charge for this ticket (set when estimate is applied). */
public enum TicketPaymentPayerType {
    TENANT,
    PROPERTY_COMPANY,
    NONE
}
