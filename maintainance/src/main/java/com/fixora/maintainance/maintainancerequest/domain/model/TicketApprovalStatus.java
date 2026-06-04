package com.fixora.maintainance.maintainancerequest.domain.model;

/** Approval lifecycle for the estimate (property admin in portal; OPERATION may override in code). */
public enum TicketApprovalStatus {
    NOT_REQUIRED,
    PENDING,
    APPROVED,
    REJECTED
}
