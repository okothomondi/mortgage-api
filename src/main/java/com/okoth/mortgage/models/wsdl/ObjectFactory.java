package com.okoth.mortgage.models.wsdl;

import com.okoth.mortgage.models.wsdl.account.Account;
import com.okoth.mortgage.models.wsdl.transaction.TransactionData;
import com.okoth.mortgage.models.wsdl.transaction.TransactionsRequest;
import com.okoth.mortgage.models.wsdl.transaction.TransactionsResponse;
import jakarta.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface
 * generated in the com.okoth.credable.stubs package.
 *
 * <p>An ObjectFactory allows you to programatically construct new instances of the Java
 * representation for XML content. The Java representation of XML content can consist of schema
 * derived interfaces and classes representing the binding of schema type definitions, element
 * declarations and model groups. Factory methods for each of these are provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

  /**
   * Create a new ObjectFactory that can be used to create new instances of schema derived classes
   * for package: com.okoth.credable.stubs
   */
  public ObjectFactory() {}

  /** Create an instance of {@link TransactionsRequest } */
  public TransactionsRequest createTransactionsRequest() {
    return new TransactionsRequest();
  }

  /** Create an instance of {@link TransactionsResponse } */
  public TransactionsResponse createTransactionsResponse() {
    return new TransactionsResponse();
  }

  /** Create an instance of {@link TransactionData } */
  public TransactionData createTransactionData() {
    return new TransactionData();
  }

  /** Create an instance of {@link Account } */
  public Account createAccount() {
    return new Account();
  }
}
