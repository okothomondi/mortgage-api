package com.okoth.mortgage.models.wsdl.account;

import com.okoth.mortgage.models.wsdl.enums.Currency;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "account",
    propOrder = {
      "accountNumber",
      "createdAt",
      "createdDate",
      "currency",
      "customerId",
      "customerNumber",
      "id",
      "ownership",
      "productName",
      "runningBalance",
      "updatedAt",
      "updatedDate"
    })
public class Account {

  protected String accountNumber;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar createdAt;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar createdDate;

  @XmlSchemaType(name = "string")
  protected Currency currency;

  protected Long customerId;
  protected String customerNumber;
  protected Long id;
  protected String ownership;
  protected String productName;
  protected double runningBalance;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar updatedAt;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar updatedDate;

  /**
   * Gets the value of the accountNumber property.
   *
   * @return possible object is {@link String }
   */
  public String getAccountNumber() {
    return accountNumber;
  }

  /**
   * Sets the value of the accountNumber property.
   *
   * @param value allowed object is {@link String }
   */
  public void setAccountNumber(String value) {
    this.accountNumber = value;
  }

  /**
   * Gets the value of the createdAt property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getCreatedAt() {
    return createdAt;
  }

  /**
   * Sets the value of the createdAt property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setCreatedAt(XMLGregorianCalendar value) {
    this.createdAt = value;
  }

  /**
   * Gets the value of the createdDate property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getCreatedDate() {
    return createdDate;
  }

  /**
   * Sets the value of the createdDate property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setCreatedDate(XMLGregorianCalendar value) {
    this.createdDate = value;
  }

  /**
   * Gets the value of the currency property.
   *
   * @return possible object is {@link Currency }
   */
  public Currency getCurrency() {
    return currency;
  }

  /**
   * Sets the value of the currency property.
   *
   * @param value allowed object is {@link Currency }
   */
  public void setCurrency(Currency value) {
    this.currency = value;
  }

  /**
   * Gets the value of the customerId property.
   *
   * @return possible object is {@link Long }
   */
  public Long getCustomerId() {
    return customerId;
  }

  /**
   * Sets the value of the customerId property.
   *
   * @param value allowed object is {@link Long }
   */
  public void setCustomerId(Long value) {
    this.customerId = value;
  }

  /**
   * Gets the value of the customerNumber property.
   *
   * @return possible object is {@link String }
   */
  public String getCustomerNumber() {
    return customerNumber;
  }

  /**
   * Sets the value of the customerNumber property.
   *
   * @param value allowed object is {@link String }
   */
  public void setCustomerNumber(String value) {
    this.customerNumber = value;
  }

  /**
   * Gets the value of the id property.
   *
   * @return possible object is {@link Long }
   */
  public Long getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   *
   * @param value allowed object is {@link Long }
   */
  public void setId(Long value) {
    this.id = value;
  }

  /**
   * Gets the value of the ownership property.
   *
   * @return possible object is {@link String }
   */
  public String getOwnership() {
    return ownership;
  }

  /**
   * Sets the value of the ownership property.
   *
   * @param value allowed object is {@link String }
   */
  public void setOwnership(String value) {
    this.ownership = value;
  }

  /**
   * Gets the value of the productName property.
   *
   * @return possible object is {@link String }
   */
  public String getProductName() {
    return productName;
  }

  /**
   * Sets the value of the productName property.
   *
   * @param value allowed object is {@link String }
   */
  public void setProductName(String value) {
    this.productName = value;
  }

  /** Gets the value of the runningBalance property. */
  public double getRunningBalance() {
    return runningBalance;
  }

  /** Sets the value of the runningBalance property. */
  public void setRunningBalance(double value) {
    this.runningBalance = value;
  }

  /**
   * Gets the value of the updatedAt property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getUpdatedAt() {
    return updatedAt;
  }

  /**
   * Sets the value of the updatedAt property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setUpdatedAt(XMLGregorianCalendar value) {
    this.updatedAt = value;
  }

  /**
   * Gets the value of the updatedDate property.
   *
   * @return possible object is {@link XMLGregorianCalendar }
   */
  public XMLGregorianCalendar getUpdatedDate() {
    return updatedDate;
  }

  /**
   * Sets the value of the updatedDate property.
   *
   * @param value allowed object is {@link XMLGregorianCalendar }
   */
  public void setUpdatedDate(XMLGregorianCalendar value) {
    this.updatedDate = value;
  }
}
