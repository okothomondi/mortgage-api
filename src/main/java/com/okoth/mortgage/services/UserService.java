package com.okoth.mortgage.services;

import com.okoth.mortgage.exceptions.BadRequestException;
import com.okoth.mortgage.exceptions.ResourceNotFoundException;
import com.okoth.mortgage.exceptions.UnauthorizedException;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.enums.Role;
import com.okoth.mortgage.repositories.UserRepository;
import com.okoth.mortgage.spring.security.JwtUtils;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
  private final JwtUtils jwtUtils;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public List<UserDTO> getAllUsers() {
    return userRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
  }

  public UserDTO getUserById(Long id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
    return convertToDto(user);
  }

  public UserDTO getUserByUserName(String jwt) {
    if (jwtUtils.validateJwtToken(jwt)) {
      User user =
          userRepository
              .findByUsername(jwtUtils.getUserNameFromJwtToken(jwt))
              .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
      return convertToDto(user);
    } else throw new UnauthorizedException("Invalid JWT token");
  }

  @Transactional
  public List<UserDTO> getAllUsers(User currentUser) {
    if (!currentUser.getRole().equals(Role.ADMIN))
      throw new UnauthorizedException("ACTION_NOT_ALLOWED");
    return userRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
  }

  @Transactional
  public UserDTO getUserById(Long id, User currentUser) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));

    if (!currentUser.getId().equals(id) && !currentUser.getRole().equals(Role.ADMIN))
      throw new UnauthorizedException("ACTION_NOT_ALLOWED");
    return convertToDto(user);
  }

  @Transactional
  public UserDTO createUser(UserDTO request, User currentUser) {
    if (!currentUser.getRole().equals(Role.ADMIN))
      throw new UnauthorizedException("Only admin can create users");

    if (userRepository.existsByUsername(request.getUsername()))
      throw new BadRequestException("Username is already taken");

    if (userRepository.existsByEmail(request.getEmail()))
      throw new BadRequestException("Email is already in use");

    User user = new User(request, passwordEncoder.encode(request.getPassword()));
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    return convertToDto(userRepository.save(user));
  }

  @Transactional
  public void deleteUser(Long id, User currentUser) {
    if (!currentUser.getRole().equals(Role.ADMIN))
      throw new UnauthorizedException("Only admin can delete users");
    if (currentUser.getId().equals(id)) throw new BadRequestException("ACTION_NOT_ALLOWED");
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("USER_NOT_FOUND"));
    userRepository.delete(user);
  }

  private UserDTO convertToDto(User user) {
    return new UserDTO(user);
  }
}
