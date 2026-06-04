package com.fixora.maintainance.maintainancerequest.domain.exception;

/**
 * Thrown when an operation is not valid for the ticket's current {@link com.fixora.maintainance.maintainancerequest.domain.model.TicketStatus}.
 */
public class InvalidWorkflowStateException extends MaintenanceWorkflowException {

    public InvalidWorkflowStateException(String message) {
        super(message);
    }
}
