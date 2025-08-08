package com.okoth.mortgage.repositories;

import com.okoth.mortgage.models.db.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);

  Optional<User> findByEmail(String email);

  @Query("select u from User u where u.nationalId = ?1")
  Optional<User> findByNationalId(String nationalId);

  @Query("select u from User u where u.email = ?1 or u.username = ?2")
  Optional<User> findByEmailOrUsername(String email, String username);

  Boolean existsByUsername(String username);

  Boolean existsByEmail(String email);
}
