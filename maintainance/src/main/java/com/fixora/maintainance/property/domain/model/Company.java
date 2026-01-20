package com.fixora.maintainance.property.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class Company {

    private Long id;

    private String name;

    private String email;
    private String phone;
    private String address;
    private String companyCode;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Domain model references - using IDs to avoid circular dependencies
    // Infrastructure layer will handle loading full entities when needed
    private List<Long> userIds;

    private List<Long> buildingIds;

}

