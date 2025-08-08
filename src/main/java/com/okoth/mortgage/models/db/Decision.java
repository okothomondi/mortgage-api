package com.okoth.mortgage.models.db;

import com.okoth.mortgage.models.dto.DecisionDto;
import com.okoth.mortgage.models.enums.DecisionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "decisions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Decision {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private DecisionStatus status;

  @Column private String comments;

  @Column(nullable = false)
  private String approverId;

  @Column(nullable = false)
  private LocalDateTime decisionDate;

  @OneToOne
  @JoinColumn(name = "application_id", nullable = false)
  private Application application;

  public Decision(DecisionDto decisionDto) {
    this.decisionDate = LocalDateTime.now();
    this.status = decisionDto.getStatus();
    this.comments = decisionDto.getComments();
  }
}
