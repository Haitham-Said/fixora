package com.fixora.maintainance.user.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserCode {
    private Long id;
    private Long userId;
    private String code;
    private String status; // ACTIVE or INACTIVE
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime usedAt;
    private Boolean isUsed = false;
}

