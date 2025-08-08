package com.okoth.mortgage.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.okoth.mortgage.exceptions.UnauthorizedException;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.AuthRequest;
import com.okoth.mortgage.models.dto.AuthResponse;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.wsdl.customer.Customer;
import com.okoth.mortgage.models.wsdl.customer.CustomerResponse;
import com.okoth.mortgage.repositories.UserRepository;
import com.okoth.mortgage.services.externals.CoreBankingService;
import com.okoth.mortgage.spring.security.JwtUtils;
import com.okoth.mortgage.spring.security.UserDetailsImpl;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthenticationServiceTest {

  @Mock private JwtUtils jwtUtils;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private CoreBankingService coreBankingService;
  @Mock private AuthenticationManager authenticationManager;

  @InjectMocks private AuthenticationService authenticationService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void authenticateUser_validCredentials_returnsAuthResponse() {
    // Arrange
    AuthRequest request = new AuthRequest("john_doe", "securePassword");

    Authentication mockAuthentication = mock(Authentication.class);
    UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(mockAuthentication);

    when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);
    when(mockUserDetails.getUsername()).thenReturn("john_doe");
    // when(mockUserDetails.getAuthorities()).thenReturn(Collections.singleton(new
    // SimpleGrantedAuthority("ROLE_USER")));

    when(jwtUtils.generateJwtToken(mockAuthentication)).thenReturn("jwt-token");

    // Act
    AuthResponse response = authenticationService.authenticateUser(request);

    // Assert
    assertNotNull(response);
    assertEquals("jwt-token", response.getToken());
    assertEquals("john_doe", response.getUsername());
    assertEquals("ROLE_USER", response.getRole());
  }

  @Test
  void authenticateUser_invalidCredentials_throwsUnauthorizedException() {
    // Arrange
    AuthRequest request = new AuthRequest("invalid_user", "badPass");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenThrow(new RuntimeException("Authentication failed"));

    // Act + Assert
    UnauthorizedException exception =
        assertThrows(
            UnauthorizedException.class, () -> authenticationService.authenticateUser(request));

    assertEquals("Invalid username/password supplied", exception.getMessage());
  }

  @Test
  void subscribe_validUser_returnsSuccessMessage() {
    // Arrange
    UserDTO request = new UserDTO();
    request.setEmail("jane@example.com");
    request.setUsername("jane_doe");
    request.setPassword("password123");
    request.setPhoneNumber("0712345678");

    when(userRepository.findByEmailOrUsername("jane@example.com", "jane_doe"))
        .thenReturn(Optional.empty());

    CustomerResponse mockResponse = new CustomerResponse();
    mockResponse.setCustomer(new Customer()); // mock empty customer

    when(coreBankingService.getCustomer(request)).thenReturn(mockResponse);
    when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");

    // Act
    String result = authenticationService.subscribe(request);

    // Assert
    assertEquals("User successfully subscribed", result);
    verify(userRepository).save(any(User.class));
  }

  @Test
  void subscribe_existingUser_throwsBadRequestException() {
    // Arrange
    UserDTO request = new UserDTO();
    request.setEmail("existing@example.com");
    request.setUsername("existing_user");

    when(userRepository.findByEmailOrUsername("existing@example.com", "existing_user"))
        .thenReturn(Optional.of(new User()));

    // Act
    String result = authenticationService.subscribe(request);

    // Assert
    assertEquals("Invalid request", result);
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void subscribe_externalServiceThrowsException_returnsInvalidRequest() {
    // Arrange
    UserDTO request = new UserDTO();
    request.setEmail("new@example.com");
    request.setUsername("new_user");

    when(userRepository.findByEmailOrUsername("new@example.com", "new_user"))
        .thenReturn(Optional.empty());

    when(coreBankingService.getCustomer(request))
        .thenThrow(new RuntimeException("CBS service unavailable"));

    // Act
    String result = authenticationService.subscribe(request);

    // Assert
    assertEquals("Invalid request", result);
    verify(userRepository, never()).save(any(User.class));
  }
}
