package com.okoth.mortgage.services.externals;

import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.wsdl.customer.Customer;
import com.okoth.mortgage.models.wsdl.customer.CustomerPort;
import com.okoth.mortgage.models.wsdl.customer.CustomerPortService;
import com.okoth.mortgage.models.wsdl.customer.CustomerRequest;
import com.okoth.mortgage.models.wsdl.customer.CustomerResponse;
import com.okoth.mortgage.models.wsdl.transaction.TransactionDataPort;
import com.okoth.mortgage.models.wsdl.transaction.TransactionDataPortService;
import com.okoth.mortgage.models.wsdl.transaction.TransactionsRequest;
import com.okoth.mortgage.models.wsdl.transaction.TransactionsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreBankingService {

  private final CustomerPortService customerService;
  private final TransactionDataPortService transactionService;

  public TransactionsResponse getTransactions(String id) {
    TransactionsRequest request = new TransactionsRequest();
    request.setCustomerNumber(id);
    try {
      TransactionDataPort port = transactionService.getTransactionDataPortSoap11();
      TransactionsResponse response = port.transactions(request);
      log.info("Transactions success | Customer: {} | Response: {}", request, response);
      return response;
    } catch (Exception e) {
      log.error("Failed to fetch transactions for customer {}: {}", id, e.getMessage(), e);
      return new TransactionsResponse();
    }
  }

  public CustomerResponse getCustomer(UserDTO user) {
    CustomerRequest request = new CustomerRequest();
    request.setCustomerNumber(user.getNationalId());
    try {
      CustomerPort port = customerService.getCustomerPortSoap11();
      CustomerResponse response = port.customer(request);
      log.info("Customer success | Customer: {} | Response: {}", request, response);
      return response;
    } catch (Exception e) {
      log.error("Failed to getCustomer({}) : {}", user, e.getMessage());
      return new CustomerResponse(new Customer(user));
    }
  }
}
