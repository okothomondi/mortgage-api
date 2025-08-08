package com.okoth.mortgage.spring.security;

import com.okoth.mortgage.exceptions.UnauthorizedException;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityContext {
  private final UserRepository userRepository;

  public User getCurrentUser() {
    return userRepository
        .findByUsername(getUsername())
        .orElseThrow(() -> new UnauthorizedException("User not found with username"));
  }

  public String getUsername() {
    UserDetailsImpl userDetails =
        (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return userDetails.getUsername();
  }
}
