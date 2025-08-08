package com.okoth.mortgage.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationDto {
  private UUID id;
  private String nationalId;

  @NotNull(message = "Loan amount is required")
  @Positive(message = "Loan amount must be positive")
  private Double loanAmount;

  @NotNull(message = "Loan term is required")
  @Positive(message = "Loan term must be positive")
  private Integer loanTermYears;

  @NotNull(message = "Annual income is required")
  @Positive(message = "Annual income must be positive")
  private Double annualIncome;

  private ApplicationStatus status;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
  private List<DocumentDto> documents;
  private DecisionDto decision;

  public ApplicationDto(Application application) {
    this.id = application.getId();
    this.nationalId = application.getAssigneeId().getNationalId();
    this.loanAmount = application.getLoanAmount();
    this.loanTermYears = application.getLoanTermYears();
  }
}
