package com.fixora.maintainance.property.domain.model.requests;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CompanyRequest {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String companyCode;
}

