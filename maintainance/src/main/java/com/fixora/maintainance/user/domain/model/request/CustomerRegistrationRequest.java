package com.fixora.maintainance.user.domain.model.request;

import lombok.Data;

import java.util.List;

@Data
public class CustomerRegistrationRequest {
    private String name;
    private String email;
    private String phone;
    private Long companyId;
    private Long buildingId;
    private Long apartmentId;
    private List<AttachmentRequest> attachments;
}

