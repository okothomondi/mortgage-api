package com.okoth.mortgage.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.okoth.mortgage.models.enums.DecisionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecisionDto {
  private String id;

  @NotNull(message = "Decision status is required")
  private DecisionStatus status;

  private String comments;

  @NotBlank(message = "Approver ID is required")
  private String approverId;

  private String decisionDate;
}
