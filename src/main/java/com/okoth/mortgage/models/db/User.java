package com.okoth.mortgage.models.db;

import static com.okoth.mortgage.models.wsdl.enums.Status.ACTIVE;

import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.enums.Role;
import com.okoth.mortgage.models.wsdl.customer.Customer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(columnNames = "username"),
      @UniqueConstraint(columnNames = "email")
    })
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Size(max = 20)
  private String username;

  @Column(nullable = false)
  private String nationalId;

  @Column(nullable = false)
  private String firstName;

  @Column(nullable = false)
  private String lastName;

  @Column(nullable = false)
  private String phoneNumber;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private boolean isActive;

  @NotBlank
  @Size(max = 120)
  private String password;

  @Enumerated(EnumType.STRING)
  private Role role;

  @CreationTimestamp private LocalDateTime createdAt;

  public User(Long id) {
    this.id = id;
    this.role = Role.APPLICANT;
    this.createdAt = LocalDateTime.now();
    this.isActive = true;
  }

  public User(Customer customer, String password, String username, String phoneNumber) {
    this.nationalId = customer.getIdNumber();
    this.firstName = customer.getFirstName();
    this.lastName = customer.getLastName();
    this.phoneNumber = phoneNumber;
    this.username = username;
    this.email = customer.getEmail();
    this.password = password;
    this.role = Role.APPLICANT;
    this.isActive = customer.getStatus().equals(ACTIVE);
  }

  public User(Customer customer) {
    this.nationalId = customer.getIdNumber();
    this.firstName = customer.getFirstName();
    this.lastName = customer.getLastName();
    this.phoneNumber = "";
    this.username = "";
    this.email = customer.getEmail();
    this.isActive = true;
    this.password = password;
    this.role = Role.APPLICANT;
  }

  public User(UserDTO request, String password) {
    this.nationalId = request.getNationalId();
    this.firstName = request.getFirstName();
    this.lastName = request.getLastName();
    this.phoneNumber = request.getPhoneNumber();
    this.username = request.getUsername();
    this.email = request.getEmail();
    this.password = password;
    this.isActive = true;

    this.role = Role.APPLICANT;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass =
        o instanceof HibernateProxy
            ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
            : o.getClass();
    Class<?> thisEffectiveClass =
        this instanceof HibernateProxy
            ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
            : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    User user = (User) o;
    return getId() != null && Objects.equals(getId(), user.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy
        ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
        : getClass().hashCode();
  }
}
