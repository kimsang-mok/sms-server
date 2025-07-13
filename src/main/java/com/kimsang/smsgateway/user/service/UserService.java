package com.kimsang.smsgateway.user.service;

import com.kimsang.smsgateway.user.domain.User;
import com.kimsang.smsgateway.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;

  public Mono<User> getUserByUsername(final String username) {
    return userRepository.findByUsername(username)
        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.GONE, "The user account has been deleted or " +
            "inactivated")));
  }
}
