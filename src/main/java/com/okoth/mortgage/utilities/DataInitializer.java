package com.okoth.mortgage.utilities;

import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.enums.Role;
import com.okoth.mortgage.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void run(String... args) {
    User user = new User();
    user.setFirstName("System");
    user.setLastName("Administrator");
    user.setNationalId("1234567890");
    user.setPhoneNumber("0123456789");
    user.setRole(Role.OFFICER);
    user.setUsername("sys_admin");
    user.setEmail("sys.admin@hfgroup.com");
    user.setActive(true);
    user.setPassword(passwordEncoder.encode("$ecret"));
    try {
      userRepository.save(user);
    } catch (Exception e) {
      log.error("Exception in DataInitializer.run({}) : {}", user, e.getMessage());
    }
  }
}
