package com.example.bankspringboot.service;

import com.example.bankspringboot.common.ScheduledType;
import com.example.bankspringboot.domain.scheduledtransaction.ScheduledTransaction;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ScheduledTransactionCalculator {

  public Instant calculateNextRunAt(ScheduledTransaction scheduledTransaction) {
    LocalDateTime lastIntendedLocal = scheduledTransaction.getLastIntendedLocal();
    boolean isFirstRun = lastIntendedLocal == null;
    ZoneId zoneId = scheduledTransaction.getZoneId();
    ZonedDateTime cursor =
        lastIntendedLocal != null
            ? lastIntendedLocal.atZone(zoneId)
            : scheduledTransaction.getStartAtLocal().atZone(zoneId);
    ScheduledType scheduledType = scheduledTransaction.getScheduledType();
    int scheduledValue =
        scheduledType == ScheduledType.DAILY
            ? scheduledTransaction.getIntervalDays()
            : (scheduledType == ScheduledType.WEEKLY
                ? scheduledTransaction.getDayOfWeek().getValue()
                : (scheduledType == ScheduledType.MONTHLY
                    ? scheduledTransaction.getDayOfMonth()
                    : 0));
    return calculateNextSlot(cursor, scheduledType, scheduledValue, isFirstRun).toInstant();
  }

  private ZonedDateTime calculateNextSlot(
      ZonedDateTime cursor, ScheduledType scheduledType, int scheduledValue, boolean isFirstRun) {
    ZonedDateTime now = ZonedDateTime.now(cursor.getZone());
    switch (scheduledType) {
      case DAILY:
        cursor = cursor.plusDays(scheduledValue);
        break;
      case WEEKLY:
        if (isFirstRun) {
          int gap = (scheduledValue - cursor.getDayOfWeek().getValue() + 7) % 7;
          if (gap == 0 && now.isAfter(cursor)) {
            gap = 7;
          }
          cursor = cursor.plusDays(gap);
        } else {
          cursor = cursor.plusWeeks(1);
        }
        break;
      case MONTHLY:
        if (isFirstRun) {
          YearMonth ym = YearMonth.from(cursor);
          int dom = Math.min(ym.lengthOfMonth(), scheduledValue);
          ZonedDateTime candidate = cursor.withDayOfMonth(dom);
          if (now.isAfter(candidate)) {
            candidate =
                candidate
                    .plusMonths(1)
                    .withDayOfMonth(
                        Math.min(YearMonth.from(candidate).lengthOfMonth(), scheduledValue));
          }
          cursor = candidate;
        } else {
          cursor = cursor.plusMonths(1);
        }
        break;
      default:
        break;
    }
    return cursor;
  }
}
