package com.fixora.maintainance.maintainancerequest.domain.exception;

/**
 * Thrown when the caller's company or role does not allow the workflow action.
 */
public class WorkflowAccessDeniedException extends MaintenanceWorkflowException {

    public WorkflowAccessDeniedException(String message) {
        super(message);
    }
}
