package com.fixora.security.inbound.model;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequest(
        @NotBlank(message = "userName is required")
        String userName,
        @NotBlank(message = "password is required")
        String password
){ }
