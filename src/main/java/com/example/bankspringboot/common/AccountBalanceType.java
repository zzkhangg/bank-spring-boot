package com.example.bankspringboot.common;

import java.math.BigDecimal;

public enum AccountBalanceType {

    HIGH(new BigDecimal("100000000"), null),              // balance >= 100,000,000
    MEDIUM(new BigDecimal("10000000"), new BigDecimal("100000000")), // 10M → 100M
    LOW(BigDecimal.ZERO, new BigDecimal("10000000"));    // < 10M

    private final BigDecimal min;
    private final BigDecimal max;

    AccountBalanceType(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }
}

