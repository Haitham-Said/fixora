package com.fixora.maintainance.user.domain.model;

public enum Role {
    CUSTOMER,
    MAINTAINER,
    EMPLOYEE,
    ADMIN,
    /** Facility-management company admin (portal); may estimate when workflow says FACILITY_ADMIN. */
    FM_ADMIN,
    OPERATION
}
