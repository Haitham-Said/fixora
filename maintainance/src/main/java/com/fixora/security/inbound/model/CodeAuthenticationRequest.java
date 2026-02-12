package com.fixora.security.inbound.model;

import jakarta.validation.constraints.NotBlank;

public record CodeAuthenticationRequest(
        @NotBlank(message = "email is required")
        String email,
        @NotBlank(message = "code is required")
        String code
) { }

