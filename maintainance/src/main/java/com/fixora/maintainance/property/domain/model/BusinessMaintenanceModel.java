package com.fixora.maintainance.property.domain.model;

/**
 * How a {@link CompanyType#PROPERTY_MANAGEMENT} portfolio handles maintenance execution.
 * Configured on {@link CompanyWorkflowConfig}, not on individual tickets.
 */
public enum BusinessMaintenanceModel {
    /** PM company uses its own maintainer pool ({@code executorCompanyId = pmCompanyId}). */
    INTERNAL_MAINTENANCE,
    /** PM routes work to a contracted FM company ({@code executorCompanyId = facilityManagementCompanyId}). */
    FACILITY_MANAGEMENT
}
