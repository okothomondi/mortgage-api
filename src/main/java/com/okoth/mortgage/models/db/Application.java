package com.okoth.mortgage.models.db;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.okoth.mortgage.models.dto.ApplicationDto;
import com.okoth.mortgage.models.enums.ApplicationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "applications")
public class Application {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "assignee_id")
  @ToString.Exclude
  private User assigneeId;

  @Column(nullable = false)
  private Double loanAmount;

  @Column(nullable = false)
  private Integer loanTermYears;

  @Column(nullable = false)
  private Double annualIncome;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ApplicationStatus status;

  @Column(nullable = false)
  private LocalDateTime createdAt;

  @Column(nullable = false)
  private LocalDateTime updatedAt;

  private Double requestedAmount;
  private Double approvedAmount;
  private Integer score;
  private Double limitAmount;
  private String exclusion;
  private String exclusionReason;
  private String scoringToken;
  private Integer retryCount;
  private LocalDateTime disbursedAt;

  @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Document> documents;

  @OneToOne(mappedBy = "application", cascade = CascadeType.ALL)
  private Decision decision;

  public Application(User customer, Double requestedAmount, ApplicationStatus status) {
    this.status = status;
    this.assigneeId = customer;
    this.requestedAmount = requestedAmount;
  }

  public Application(ApplicationDto request, User assigneeId) {
    this.loanAmount = request.getLoanAmount();
    this.loanTermYears = request.getLoanTermYears();
    this.annualIncome = request.getAnnualIncome();
    this.assigneeId = assigneeId;
    LocalDateTime now = LocalDateTime.now();
    this.createdAt = now;
    this.updatedAt = now;
    this.status = ApplicationStatus.SUBMITTED;
  }
}
