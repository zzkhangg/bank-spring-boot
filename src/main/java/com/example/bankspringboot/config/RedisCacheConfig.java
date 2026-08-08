package com.example.bankspringboot.config;

import com.example.bankspringboot.dto.account.AccountSummaryResponse;
import com.example.bankspringboot.dto.common.PageResponse;
import com.example.bankspringboot.dto.statistics.TransactionReportResponse;
import com.example.bankspringboot.dto.transaction.TransactionResponse;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * Redis cache wiring.
 *
 * <p>Every cache declares the exact type it stores. This is deliberately more verbose than handing
 * Redis one generic serializer: a generic serializer has to embed Java class names in the stored
 * JSON to know what to rebuild, and even then it cannot round-trip the element type of a generic
 * collection such as {@code List<AccountSummaryResponse>} — those come back as untyped maps and blow
 * up at the call site. Naming the type per cache keeps the stored value plain, readable JSON that
 * can be inspected with {@code redis-cli} and removes that class of bug entirely.
 *
 * <p>TTL and null-value policy come from {@code spring.cache.redis.*} so they stay configurable per
 * environment.
 */
@Configuration
public class RedisCacheConfig {

  @Bean
  public RedisCacheManagerBuilderCustomizer cacheTypeCustomizer(
      ObjectMapper objectMapper, CacheProperties cacheProperties) {

    ObjectMapper redisObjectMapper = objectMapper.copy();
    var types = redisObjectMapper.getTypeFactory();

    JavaType accountSummaryList =
        types.constructCollectionType(List.class, AccountSummaryResponse.class);
    JavaType reportList = types.constructCollectionType(List.class, TransactionReportResponse.class);
    JavaType transactionPage =
        types.constructParametricType(PageResponse.class, TransactionResponse.class);
    JavaType version = types.constructType(Long.class);

    RedisCacheConfiguration base = baseConfiguration(cacheProperties);

    return builder ->
        builder
            .withCacheConfiguration(
                "accountsByUser", withType(base, redisObjectMapper, accountSummaryList))
            .withCacheConfiguration(
                "transactionHistory", withType(base, redisObjectMapper, transactionPage))
            .withCacheConfiguration(
                "weeklyTransactionReport", withType(base, redisObjectMapper, reportList))
            .withCacheConfiguration(
                "quarterlyTransactionReport", withType(base, redisObjectMapper, reportList))
            .withCacheConfiguration(
                "yearlyTransactionReport", withType(base, redisObjectMapper, reportList))
            // The version counter deliberately does not expire. It is the invalidation key for
            // "transactionHistory"; if it expired first the counter would reset to 0 and a stale
            // page cached under the old version could be served again.
            .withCacheConfiguration(
                "transactionHistoryVersion",
                withType(base.entryTtl(java.time.Duration.ZERO), redisObjectMapper, version));
  }

  private RedisCacheConfiguration baseConfiguration(CacheProperties cacheProperties) {
    CacheProperties.Redis redis = cacheProperties.getRedis();
    RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();

    if (redis.getTimeToLive() != null) {
      configuration = configuration.entryTtl(redis.getTimeToLive());
    }
    if (!redis.isCacheNullValues()) {
      configuration = configuration.disableCachingNullValues();
    }
    if (!redis.isUseKeyPrefix()) {
      configuration = configuration.disableKeyPrefix();
    }
    if (redis.getKeyPrefix() != null) {
      configuration = configuration.prefixCacheNameWith(redis.getKeyPrefix());
    }
    return configuration;
  }

  private RedisCacheConfiguration withType(
      RedisCacheConfiguration base, ObjectMapper objectMapper, JavaType type) {
    return base.serializeValuesWith(
        RedisSerializationContext.SerializationPair.fromSerializer(
            new Jackson2JsonRedisSerializer<>(objectMapper, type)));
  }
}
