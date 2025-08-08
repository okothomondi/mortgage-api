package com.okoth.mortgage.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.okoth.mortgage.exceptions.BadRequestException;
import com.okoth.mortgage.exceptions.ResourceNotFoundException;
import com.okoth.mortgage.exceptions.UnauthorizedException;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.enums.Role;
import com.okoth.mortgage.repositories.UserRepository;
import com.okoth.mortgage.spring.security.JwtUtils;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

  @Mock private JwtUtils jwtUtils;
  @Mock private UserRepository userRepository;
  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private UserService userService;

  private User adminUser;
  private User normalUser;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    adminUser = new User();
    adminUser.setId(1L);
    adminUser.setUsername("admin");
    adminUser.setRole(Role.ADMIN);

    normalUser = new User();
    normalUser.setId(2L);
    normalUser.setUsername("user");
    normalUser.setRole(Role.USER);
  }

  @Test
  void getAllUsers_shouldReturnList() {
    when(userRepository.findAll()).thenReturn(List.of(adminUser, normalUser));
    List<UserDTO> users = userService.getAllUsers();
    assertEquals(2, users.size());
  }

  @Test
  void getUserById_whenFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
    UserDTO dto = userService.getUserById(1L);
    assertEquals("admin", dto.getUsername());
  }

  @Test
  void getUserById_whenNotFound_shouldThrow() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
  }

  @Test
  void getUserByUserName_withValidJwt() {
    when(jwtUtils.validateJwtToken("valid.token")).thenReturn(true);
    when(jwtUtils.getUserNameFromJwtToken("valid.token")).thenReturn("admin");
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(adminUser));

    UserDTO result = userService.getUserByUserName("valid.token");
    assertEquals("admin", result.getUsername());
  }

  @Test
  void getUserByUserName_withInvalidJwt_shouldThrow() {
    when(jwtUtils.validateJwtToken("bad.token")).thenReturn(false);
    assertThrows(UnauthorizedException.class, () -> userService.getUserByUserName("bad.token"));
  }

  @Test
  void getAllUsers_asAdmin_shouldSucceed() {
    when(userRepository.findAll()).thenReturn(List.of(adminUser, normalUser));
    List<UserDTO> result = userService.getAllUsers(adminUser);
    assertEquals(2, result.size());
  }

  @Test
  void getAllUsers_asUser_shouldThrow() {
    assertThrows(UnauthorizedException.class, () -> userService.getAllUsers(normalUser));
  }

  @Test
  void getUserById_asSelf_shouldSucceed() {
    when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
    UserDTO result = userService.getUserById(2L, normalUser);
    assertEquals("user", result.getUsername());
  }

  @Test
  void getUserById_asAdmin_shouldSucceed() {
    when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
    UserDTO result = userService.getUserById(2L, adminUser);
    assertEquals("user", result.getUsername());
  }

  @Test
  void getUserById_asOtherUser_shouldThrow() {
    when(userRepository.findById(1L)).thenReturn(Optional.of(adminUser));
    assertThrows(UnauthorizedException.class, () -> userService.getUserById(1L, normalUser));
  }

  @Test
  void createUser_asAdmin_successful() {
    UserDTO dto = new UserDTO();
    dto.setUsername("newuser");
    dto.setEmail("new@user.com");
    dto.setPassword("pass");

    when(userRepository.existsByUsername("newuser")).thenReturn(false);
    when(userRepository.existsByEmail("new@user.com")).thenReturn(false);
    when(passwordEncoder.encode("pass")).thenReturn("encoded");

    User saved = new User(dto, "encoded");
    when(userRepository.save(any(User.class))).thenReturn(saved);

    UserDTO result = userService.createUser(dto, adminUser);
    assertEquals("newuser", result.getUsername());
  }

  @Test
  void createUser_asUser_shouldThrow() {
    UserDTO dto = new UserDTO();
    dto.setUsername("newuser");
    assertThrows(UnauthorizedException.class, () -> userService.createUser(dto, normalUser));
  }

  @Test
  void createUser_withExistingUsername_shouldThrow() {
    UserDTO dto = new UserDTO();
    dto.setUsername("existing");
    dto.setEmail("email@example.com");

    when(userRepository.existsByUsername("existing")).thenReturn(true);
    assertThrows(BadRequestException.class, () -> userService.createUser(dto, adminUser));
  }

  @Test
  void createUser_withExistingEmail_shouldThrow() {
    UserDTO dto = new UserDTO();
    dto.setUsername("unique");
    dto.setEmail("taken@email.com");

    when(userRepository.existsByUsername("unique")).thenReturn(false);
    when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);

    assertThrows(BadRequestException.class, () -> userService.createUser(dto, adminUser));
  }

  @Test
  void deleteUser_asAdmin_shouldSucceed() {
    when(userRepository.findById(2L)).thenReturn(Optional.of(normalUser));
    userService.deleteUser(2L, adminUser);
    verify(userRepository).delete(normalUser);
  }

  @Test
  void deleteUser_asUser_shouldThrow() {
    assertThrows(UnauthorizedException.class, () -> userService.deleteUser(1L, normalUser));
  }

  @Test
  void deleteUser_deletingSelf_shouldThrow() {
    assertThrows(BadRequestException.class, () -> userService.deleteUser(1L, adminUser));
  }

  @Test
  void deleteUser_userNotFound_shouldThrow() {
    when(userRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(99L, adminUser));
  }
}
