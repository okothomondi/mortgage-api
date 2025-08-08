package com.okoth.mortgage.repositories;

import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.enums.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository
    extends JpaRepository<Application, UUID>, JpaSpecificationExecutor<Application> {
  @Query(
      """
      SELECT l FROM Application l WHERE l.assigneeId = :assigneeId
      AND l.status IN ('PENDING', 'PROCESSING', 'APPROVED', 'OVERDUE')""")
  Optional<Application> findActiveByCustomerNumber(@Param("assigneeId") User assigneeId);

  Page<Application> findByStatus(ApplicationStatus status, Pageable pageable);

  Page<Application> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

  List<Application> findByStatus(ApplicationStatus status);

  @Query("select l from Application l where l.assigneeId = ?1")
  Optional<Application> getByCustomerNumber(User assigneeId);

  @Query("select l from Application l where l.disbursedAt > ?1")
  List<Application> findLoansOlderThan(LocalDateTime disbursedAt);

  @Query("select l from Application l where l.assigneeId = ?1 and l.status = ?2")
  Optional<Application> getByCustomerAndStatus(User assigneeId, ApplicationStatus status);

  @Query("select (count(l) > 0) from Application l where l.assigneeId = ?1 and l.status = ?2")
  boolean existsByCustomerAndStatus(User assigneeId, ApplicationStatus status);
}
