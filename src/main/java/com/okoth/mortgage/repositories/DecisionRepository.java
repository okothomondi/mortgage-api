package com.okoth.mortgage.repositories;

import com.okoth.mortgage.models.db.Decision;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DecisionRepository extends JpaRepository<Decision, UUID> {

  Optional<Decision> findByApplicationId(UUID applicationId);

  boolean existsByApplicationId(UUID applicationId);

  void deleteByApplicationId(UUID applicationId);
}
