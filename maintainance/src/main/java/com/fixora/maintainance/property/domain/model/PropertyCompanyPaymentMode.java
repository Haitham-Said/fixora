package com.fixora.maintainance.property.domain.model;

/**
 * How the property company settles maintenance charges with Fixora (MVP coarse switch).
 * Tenant prepayment before assign is a separate flag on config ({@code tenantPrepaymentRequired} / {@code payment_required} column).
 */
public enum PropertyCompanyPaymentMode {
    COMPANY_BILLED,
    NOT_REQUIRED_FOR_MVP
}
