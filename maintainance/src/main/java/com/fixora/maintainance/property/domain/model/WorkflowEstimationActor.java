package com.fixora.maintainance.property.domain.model;

/**
 * Portal-only estimation (MVP): who may enter the estimate — never the WhatsApp tenant or field maintainer.
 * Maps to JWT roles {@code ADMIN} (property) vs {@code FM_ADMIN} (facility management).
 */
public enum WorkflowEstimationActor {
    PROPERTY_ADMIN,
    FACILITY_ADMIN,
    /**
     * No explicit portal estimation actor on config. Not used when {@link PropertyCompanyPaymentMode#COMPANY_BILLED}
     * — domain infers PROPERTY_ADMIN or FACILITY_ADMIN for mandatory cost tracking.
     */
    NONE
}
