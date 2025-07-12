package com.kimsang.smsgateway.config;

import com.kimsang.smsgateway.auth.security.JwtService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.*;
import reactor.core.publisher.Mono;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;

@Configuration
@Setter
@Getter
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

  private RSAPrivateKey privateKey;
  private RSAPublicKey publicKey;
  private Duration ttl;

  @Bean
  public JwtEncoder jwtEncoder() {
    var jwk = new RSAKey.Builder(publicKey)
        .privateKey(privateKey)
        .build();

    var jwkSet = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwkSet);
  }

  @Bean
  public ReactiveJwtDecoder reactiveJwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
    return token -> Mono.fromCallable(() -> decoder.decode(token));
  }

  @Bean
  public JwtService jwtService(
      @Value("${spring.application.name}") String appName,
      JwtEncoder jwtEncoder
  ) {
    return new JwtService(appName, ttl, jwtEncoder);
  }
}
