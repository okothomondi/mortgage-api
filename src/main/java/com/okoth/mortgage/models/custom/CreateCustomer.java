package com.okoth.mortgage.models.custom;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CreateCustomer {
  long id;
  String url;
  String name;
  String userName;
  String password;
  String token;

  public CreateCustomer(String url) {
    this.url = url;
  }
}
