package com.fixora.maintainance.user.domain.model.request;

import lombok.Data;

@Data
public class UserActivationRequest {
    private Long userId;
    // No password needed - user will set password using activation code
}

