package com.fixora.maintainance.user.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class InactiveUser {
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private Long companyId;
    private String companyName;
    private Long buildingId;
    private String buildingName;
    private Long apartmentId;
    private String apartmentNumber;
    private LocalDateTime registeredAt;
    private List<Attachment> attachments;
}

