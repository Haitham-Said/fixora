package com.fixora.maintainance.maintainancerequest.domain.model;

/**
 * Maintenance request lifecycle (MVP). Domain-only names — DB stores same enum strings (see migration for renames).
 */
public enum TicketStatus {

    CREATED,
    /** Waiting for a portal user to submit an estimate (never maintainer / tenant). */
    NEEDS_ESTIMATION,
    AWAITING_APPROVAL,
    AWAITING_TENANT_PAYMENT,
    /** Eligible for automatic or manual maintainer assignment. */
    READY_TO_ASSIGN,

    ASSIGNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    REJECTED,

    /** Automatic assignment failed after max retries; operations assigns manually (still {@link #READY_TO_ASSIGN} semantics for workflow gates). */
    MANUAL_ASSIGNMENT
}
