package com.example.bankspringboot.dto.statistics;

import java.math.BigDecimal;

/**
 * Concrete, serializable view of an aggregated transaction report.
 *
 * <p>The repository returns {@link TransactionReportView}, a Spring Data interface projection. Those
 * are runtime proxies: fine to hand straight to Jackson on the way out, but impossible to rebuild
 * when read back from a cache. Since these reports are cached in Redis they are copied into this
 * record first. The JSON field names are unchanged, so the HTTP response is identical.
 */
public record TransactionReportResponse(
    Integer year, Integer period, BigDecimal avgAmount, BigDecimal maxAmount, BigDecimal minAmount) {

  public static TransactionReportResponse from(TransactionReportView view) {
    return new TransactionReportResponse(
        view.getYear(),
        view.getPeriod(),
        view.getAvgAmount(),
        view.getMaxAmount(),
        view.getMinAmount());
  }
}
