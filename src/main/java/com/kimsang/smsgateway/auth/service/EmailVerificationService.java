package com.kimsang.smsgateway.auth.service;

import com.kimsang.smsgateway.common.util.CryptoUtil;
import com.kimsang.smsgateway.user.domain.User;
import com.kimsang.smsgateway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailVerificationService {
  @Value("${email-verification.base-url}")
  private String baseUrl;

  private final OtpService otpService;

  private final CryptoUtil cryptoUtil;

  private final UserRepository userRepository;

  private final JavaMailSender mailSender;

  private final TransactionalOperator txOperator;

  public Mono<Void> sendVerificationToken(UUID userId, String email) {
    return otpService.generateAndStoreOtp(userId)
        .flatMap(token -> {
          final String encryptedUserId = cryptoUtil.encrypt(userId.toString());
          final String emailVerificationUrl = baseUrl.formatted(URLEncoder.encode(encryptedUserId,
                  StandardCharsets.UTF_8),
              token);
          final String emailText = "Click the link to verify the email: " + emailVerificationUrl;

          log.info("Email text: {}", emailText);

          return Mono.fromRunnable(() -> {
            var message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Email Verification");
            message.setFrom("System");
            message.setText(emailText);
            mailSender.send(message);
          });
        });
  }

  public Mono<Void> resendVerificationToken(String email) {
    return userRepository.findByEmail(email)
        .filter(user -> !user.isVerified())
        .switchIfEmpty(Mono.defer(() -> {
              log.warn("Attempt to resend verification token for non existing or already validated email: [{}]", email);
              return Mono.empty();
            }
        ))
        .flatMap(user -> sendVerificationToken(user.getId(), user.getEmail()))
        .then();
  }


  public Mono<User> verifyEmail(String encryptedUserId, String token) {
    UUID userId = UUID.fromString(cryptoUtil.decrypt(encryptedUserId));

    return otpService.isOtpValid(userId, token)
        .flatMap(valid -> {
          if (!valid) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token invalid or expired"));
          }

          return otpService.deleteOtp(userId)
              .then(userRepository.findById(userId)
                  .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.GONE, "User deleted"))))
              .flatMap(user -> {
                if (user.isVerified()) {
                  return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already verified"));
                }
                user.setVerified(true);
                return userRepository.save(user);
              });
        })
        .as(txOperator::transactional);
  }
}
