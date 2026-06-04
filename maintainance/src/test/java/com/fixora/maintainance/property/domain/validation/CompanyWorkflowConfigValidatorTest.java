package com.fixora.maintainance.property.domain.validation;

import com.fixora.maintainance.property.domain.model.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CompanyWorkflowConfigValidatorTest {

    @Test
    void internal_rejectsFmCompanyId() {
        Company pm = Company.builder().id(1L).type(CompanyType.PROPERTY_MANAGEMENT).build();
        var cfg = CompanyWorkflowConfig.builder()
                .pmCompanyId(1L)
                .businessMaintenanceModel(BusinessMaintenanceModel.INTERNAL_MAINTENANCE)
                .facilityManagementCompanyId(99L)
                .build();
        assertThrows(IllegalArgumentException.class, () -> CompanyWorkflowConfigValidator.validate(cfg, pm, null));
    }

    @Test
    void facility_requiresFmCompany() {
        Company pm = Company.builder().id(1L).type(CompanyType.PROPERTY_MANAGEMENT).build();
        var cfg = CompanyWorkflowConfig.builder()
                .pmCompanyId(1L)
                .businessMaintenanceModel(BusinessMaintenanceModel.FACILITY_MANAGEMENT)
                .facilityManagementCompanyId(null)
                .build();
        assertThrows(IllegalArgumentException.class, () -> CompanyWorkflowConfigValidator.validate(cfg, pm, null));
    }

    @Test
    void facility_rejectsPmAsFmTarget() {
        Company pm = Company.builder().id(1L).type(CompanyType.PROPERTY_MANAGEMENT).build();
        var cfg = CompanyWorkflowConfig.builder()
                .pmCompanyId(1L)
                .businessMaintenanceModel(BusinessMaintenanceModel.FACILITY_MANAGEMENT)
                .facilityManagementCompanyId(1L)
                .build();
        assertThrows(IllegalArgumentException.class, () -> CompanyWorkflowConfigValidator.validate(cfg, pm, pm));
    }

    @Test
    void facility_acceptsFmCompany() {
        Company pm = Company.builder().id(1L).type(CompanyType.PROPERTY_MANAGEMENT).build();
        Company fm = Company.builder().id(99L).type(CompanyType.FACILITY_MANAGEMENT).build();
        var cfg = CompanyWorkflowConfig.builder()
                .pmCompanyId(1L)
                .businessMaintenanceModel(BusinessMaintenanceModel.FACILITY_MANAGEMENT)
                .facilityManagementCompanyId(99L)
                .build();
        assertDoesNotThrow(() -> CompanyWorkflowConfigValidator.validate(cfg, pm, fm));
    }
}
