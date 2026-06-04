package com.fixora.maintainance.property.domain.model;

/**
 * Who may approve an estimate in the portal (MVP). Kept separate from {@link WorkflowEstimationActor}
 * so FM-led estimation does not imply FM-led approval — property admin still approves spend.
 */
public enum WorkflowApprovalActor {
    PROPERTY_ADMIN,
    /** Approval step disabled for this company (or not applicable). */
    NONE
}
