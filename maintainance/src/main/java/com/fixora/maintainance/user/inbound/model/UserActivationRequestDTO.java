package com.fixora.maintainance.user.inbound.model;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserActivationRequestDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    // No password needed - user will set password using activation code
}

