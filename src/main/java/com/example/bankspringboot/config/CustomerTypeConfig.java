package com.example.bankspringboot.config;

import com.example.bankspringboot.common.CustomerTypeCode;
import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bank")
@Setter
@Getter
public class CustomerTypeConfig {
  private Map<CustomerTypeCode, TypeConfig> customerTypes = new EnumMap<>(CustomerTypeCode.class);

  @Getter
  @Setter
  public static class TypeConfig {
    private BigDecimal maxTransactionLimit;
  }
}
