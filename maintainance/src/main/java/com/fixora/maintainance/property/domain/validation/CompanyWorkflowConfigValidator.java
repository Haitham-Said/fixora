package com.fixora.maintainance.property.domain.validation;

import com.fixora.maintainance.property.domain.model.BusinessMaintenanceModel;
import com.fixora.maintainance.property.domain.model.Company;
import com.fixora.maintainance.property.domain.model.CompanyType;
import com.fixora.maintainance.property.domain.model.CompanyWorkflowConfig;

/**
 * Validates PM workflow configuration against company types and maintenance model rules.
 */
public final class CompanyWorkflowConfigValidator {

    private CompanyWorkflowConfigValidator() {
    }

    public static void validate(CompanyWorkflowConfig config, Company pmCompany, Company fmCompanyOrNull) {
        if (pmCompany == null || pmCompany.getType() != CompanyType.PROPERTY_MANAGEMENT) {
            throw new IllegalArgumentException("pmCompanyId must reference a PROPERTY_MANAGEMENT company");
        }
        if (config.getPmCompanyId() != null && !config.getPmCompanyId().equals(pmCompany.getId())) {
            throw new IllegalArgumentException("pmCompanyId must match the property management company id");
        }

        if (config.getBusinessMaintenanceModel() == BusinessMaintenanceModel.INTERNAL_MAINTENANCE) {
            if (config.getFacilityManagementCompanyId() != null) {
                throw new IllegalArgumentException(
                        "facilityManagementCompanyId must be null when businessMaintenanceModel is INTERNAL_MAINTENANCE");
            }
            return;
        }

        if (config.getBusinessMaintenanceModel() != BusinessMaintenanceModel.FACILITY_MANAGEMENT) {
            throw new IllegalArgumentException("Unsupported businessMaintenanceModel");
        }
        if (config.getFacilityManagementCompanyId() == null) {
            throw new IllegalArgumentException(
                    "facilityManagementCompanyId is required when businessMaintenanceModel is FACILITY_MANAGEMENT");
        }
        if (fmCompanyOrNull == null || fmCompanyOrNull.getType() != CompanyType.FACILITY_MANAGEMENT) {
            throw new IllegalArgumentException(
                    "facilityManagementCompanyId must reference a FACILITY_MANAGEMENT company");
        }
        if (!config.getFacilityManagementCompanyId().equals(fmCompanyOrNull.getId())) {
            throw new IllegalArgumentException("facilityManagementCompanyId does not match loaded FM company");
        }
    }
}
