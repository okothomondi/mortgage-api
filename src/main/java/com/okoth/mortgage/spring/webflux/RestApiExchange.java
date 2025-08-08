package com.okoth.mortgage.spring.webflux;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RestApiExchange {
  private String message;
  private boolean success;
}
