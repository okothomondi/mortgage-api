package com.okoth.mortgage.models.custom;

import com.okoth.mortgage.models.db.Application;
import com.okoth.mortgage.models.db.User;
import com.okoth.mortgage.models.dto.AuthResponse;
import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.wsdl.account.Account;
import com.okoth.mortgage.models.wsdl.customer.CustomerResponse;
import com.okoth.mortgage.models.wsdl.transaction.TransactionData;
import com.okoth.mortgage.models.wsdl.transaction.TransactionsResponse;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AppResponseBody {

  private AuthResponse authResponse;

  private UserDTO user;
  private List<UserDTO> users;

  boolean success;
  String message;
  Account account;
  User subscriber;
  CustomerResponse customer;
  TransactionsResponse transactions;
  TransactionData transaction;
  Application application;

  public AppResponseBody(String message) {
    this.message = message;
  }

  public AppResponseBody(User subscriber) {
    this.subscriber = subscriber;
  }
}
