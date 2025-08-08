package com.okoth.mortgage.models.custom;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CustomerScore {
  private Long id;
  private String customerNumber;
  private Integer score;
  private Double limitAmount;
  private String exclusion;
  private String exclusionReason;
}
