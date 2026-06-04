package com.fixora.maintainance.whatsapp.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TenantContext {
    private Long tenantId;
    private Long apartmentId;
    private Long buildingId;
    private Long companyId;
    private String role;
}

