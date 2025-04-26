package com.fixora.security.infrastructure.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt.config")
public record JWTConfiguration(String secret, Long expirationMs) {
}
