package com.fixora.maintainance.maintainancerequest.domain.exception;

/**
 * Base for business-rule violations in the maintenance payment/approval MVP workflow.
 */
public class MaintenanceWorkflowException extends RuntimeException {

    public MaintenanceWorkflowException(String message) {
        super(message);
    }
}
