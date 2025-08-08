package com.okoth.mortgage.services;

import com.okoth.mortgage.exceptions.BadRequestException;
import com.okoth.mortgage.exceptions.UnauthorizedException;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.AuthRequest;
import com.okoth.mortgage.models.dto.AuthResponse;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.wsdl.customer.CustomerResponse;
import com.okoth.mortgage.repositories.UserRepository;
import com.okoth.mortgage.services.externals.CoreBankingService;
import com.okoth.mortgage.spring.security.JwtUtils;
import com.okoth.mortgage.spring.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CoreBankingService coreBankingService;
  private final AuthenticationManager authenticationManager;

  /**
   * Authenticates a user based on the provided credentials and generates a JWT token for the
   * authenticated user. Stores the authentication information in the security context.
   *
   * @param request the authentication request containing the username and password of the user.
   * @return an instance of {@link AuthResponse} containing the JWT token, the username, and the
   *     role of the authenticated user.
   * @throws UnauthorizedException if authentication fails due to invalid username or password.
   */
  public AuthResponse authenticateUser(AuthRequest request) {
    try {
      Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  request.getUsername(), request.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);
      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      return new AuthResponse(
          jwtUtils.generateJwtToken(authentication),
          userDetails.getUsername(),
          userDetails.getAuthorities().iterator().next().getAuthority());
    } catch (Exception e) {
      log.error("Exception in authenticateUser({}) : {}", request, e.getMessage());
      throw new UnauthorizedException("Invalid username/password supplied");
    }
  }

  /**
   * This method is used to handle the subscription of a user by validating user details and
   * persisting the user information in the system. It ensures that the user does not already exist
   * in the database by checking their email or username and retrieves additional customer details
   * from an external system.
   *
   * @param request an instance of {@link UserDTO} containing the details of the user to be
   *     subscribed such as username, email, national ID, phone number, and password.
   * @throws BadRequestException if a user with the same email or username already exists, or if
   *     there is an issue with the customer information, or if the request is invalid.
   */
  public String subscribe(UserDTO request) {
    try {
      userRepository
          .findByEmailOrUsername(request.getEmail(), request.getUsername())
          .ifPresent(
              u -> {
                throw new BadRequestException("Username is already taken");
              });
      CustomerResponse customer = coreBankingService.getCustomer(request);
      userRepository.save(
          new User(
              customer.getCustomer(),
              passwordEncoder.encode(request.getPassword()),
              request.getUsername(),
              request.getPhoneNumber()));
      return "User successfully subscribed";
    } catch (Exception e) {
      log.error("Exception in subscribe({}) : {}", request, e.getMessage(), e);
      return "Invalid request";
    }
  }
}
