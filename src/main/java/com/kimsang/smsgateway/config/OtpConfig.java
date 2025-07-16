package com.kimsang.smsgateway.config;

import com.kimsang.smsgateway.auth.service.OtpService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

import java.time.Duration;

@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "otp")
public class OtpConfig {
  private OtpConfigProperties emailVerification;

  @Bean
  public OtpService emailVerificationOtpService(ReactiveRedisTemplate<String, String> redisTemplate) {
    return new OtpService(emailVerification, redisTemplate);
  }

  public record OtpConfigProperties(String cachePrefix, Duration ttl, Integer length, String characters) {
  }
}
