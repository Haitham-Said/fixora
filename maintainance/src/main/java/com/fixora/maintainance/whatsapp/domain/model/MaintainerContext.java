package com.fixora.maintainance.whatsapp.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MaintainerContext {
    private Long maintainerUserId;
    private Long companyId;
    private String role;
}

