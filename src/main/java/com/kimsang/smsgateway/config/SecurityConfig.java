package com.kimsang.smsgateway.config;

import com.kimsang.smsgateway.auth.security.CustomReactiveAuthenticationManager;
import com.kimsang.smsgateway.config.security.BearerTokenAccessDeniedHandler;
import com.kimsang.smsgateway.config.security.BearerTokenAuthenticationEntryPoint;
import com.kimsang.smsgateway.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
  @Bean
  public SecurityWebFilterChain securityWebFilterChain(
      final ServerHttpSecurity http,
      final BearerTokenAuthenticationEntryPoint authenticationEntryPoint,
      final BearerTokenAccessDeniedHandler accessDeniedHandler
  ) {
    return http
        .csrf(ServerHttpSecurity.CsrfSpec::disable)
        .authorizeExchange(exchange -> exchange
            .pathMatchers("/api/auth/**").permitAll()
            .pathMatchers("/actuator/**").permitAll()
            .anyExchange().authenticated()
        )
        .oauth2ResourceServer(oauth2 ->
            oauth2.jwt(Customizer.withDefaults())
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        )
        .build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public ReactiveAuthenticationManager reactiveAuthenticationManager(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder
  ) {
    return new CustomReactiveAuthenticationManager(userRepository, passwordEncoder);
  }
}
