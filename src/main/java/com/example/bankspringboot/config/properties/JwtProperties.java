package com.example.bankspringboot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        String issuer,
        String secret,
        int accessTokenExpirationMinutes,
        int refreshTokenExpirationDays
) {}
