package com.fixora.maintainance.user.domain.model.request;

import lombok.Data;

@Data
public class MaintainerRequest {
    private Long companyId;

    private String name;

    private String email;

    private String phone;

    private String role;
}
