package com.kimsang.smsgateway.auth.service;

import lombok.RequiredArgsConstructor;
import com.kimsang.smsgateway.config.OtpConfig.OtpConfigProperties;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Mono;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class OtpService {
  private static final SecureRandom SECURE_RANDOM = new SecureRandom();

  private final OtpConfigProperties configProperties;
  private final ReactiveRedisTemplate<String, String> redisTemplate;

  public Mono<String> generateAndStoreOtp(final UUID id) {
    final String otp = generateOtp(configProperties.characters(), configProperties.length());
    final String cacheKey = getCacheKey(id);

    return redisTemplate.opsForValue().set(cacheKey, otp, configProperties.ttl())
        .thenReturn(otp);
  }

  public Mono<Boolean> isOtpValid(final UUID id, final String otp) {
    final String cacheKey = getCacheKey(id);

    return redisTemplate.opsForValue()
        .get(cacheKey)
        .map(stored -> Objects.equals(stored, otp))
        .defaultIfEmpty(false);
  }

  public Mono<Boolean> deleteOtp(final UUID id) {
    final String cacheKey = getCacheKey(id);

    return redisTemplate.delete(cacheKey).map(count -> count > 0);
  }

  private String getCacheKey(UUID id) {
    return configProperties.cachePrefix().formatted(id);
  }

  public String generateOtp(String characters, Integer length) {
    StringBuilder otp = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      int index = SECURE_RANDOM.nextInt(characters.length());
      otp.append(characters.charAt(index));
    }
    return otp.toString();
  }
}
