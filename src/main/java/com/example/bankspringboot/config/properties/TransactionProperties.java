package com.example.bankspringboot.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

@ConfigurationProperties(prefix = "app.transaction")
public record TransactionProperties(
        BigDecimal feePercentage
) {}
