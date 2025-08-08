package com.okoth.mortgage.controllers;

import com.okoth.mortgage.models.custom.AppResponseBody;
import com.okoth.mortgage.models.dto.AuthRequest;
import com.okoth.mortgage.models.dto.AuthResponse;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.services.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication operations")
public class AuthenticationController {
  private final AuthenticationService authenticationService;

  @PostMapping("/login")
  @Operation(summary = "log in", description = "To loing provide valid credentials")
  public ResponseEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest request) {
    log.info("Authenticating user with username: {}", request.getUsername());
    return ResponseEntity.ok(authenticationService.authenticateUser(request));
  }

  @PostMapping("/subscribe")
  @Operation(summary = "sign up", description = "Get subscription to the Mortgage Product")
  public ResponseEntity<AppResponseBody> signUp(@Valid @RequestBody UserDTO request) {
    log.info("Registering user with username: {}", request.getUsername());
    return ResponseEntity.ok(new AppResponseBody(authenticationService.subscribe(request)));
  }
}
