package com.okoth.mortgage.models.dto;

import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class UserDTO {
  Long id;

  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank @Email private String email;

  @NotBlank
  @Size(min = 6, max = 40)
  private String password;

  @NotBlank private String nationalId;
  @NotBlank private String firstName;
  @NotBlank private String lastName;
  @NotBlank private String phoneNumber;
  private Role role;
  private LocalDateTime createdAt;

  public UserDTO(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.role = user.getRole();
    this.createdAt = user.getCreatedAt();
  }
}
