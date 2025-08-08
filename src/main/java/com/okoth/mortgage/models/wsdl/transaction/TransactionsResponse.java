package com.okoth.mortgage.models.wsdl.transaction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

/**
 * Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="transactions" type="{http://credable.io/cbs/transaction}transactionData" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"transactions"})
@XmlRootElement(name = "TransactionsResponse")
public class TransactionsResponse {

  protected List<TransactionData> transactions;

  /**
   * Gets the value of the transactions property.
   *
   * <p>This accessor method returns a reference to the live list, not a snapshot. Therefore any
   * modification you make to the returned list will be present inside the JAXB object. This is why
   * there is not a <CODE>set</CODE> method for the transactions property.
   *
   * <p>For example, to add a new item, do as follows:
   *
   * <pre>
   *    getTransactions().add(newItem);
   * </pre>
   *
   * <p>Objects of the following type(s) are allowed in the list {@link TransactionData }
   */
  public List<TransactionData> getTransactions() {
    if (transactions == null) transactions = new ArrayList<>();
    return this.transactions;
  }
}
