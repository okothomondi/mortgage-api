package com.okoth.mortgage.models.custom;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LoanRequest {
  Double amount;
  String customerNumber;
  String token;
}
