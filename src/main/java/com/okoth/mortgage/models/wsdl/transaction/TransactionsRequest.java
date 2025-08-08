package com.okoth.mortgage.models.wsdl.transaction;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
 *         &lt;element name="customerNumber" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"customerNumber"})
@XmlRootElement(name = "TransactionsRequest")
public class TransactionsRequest {

  @XmlElement(required = true)
  protected String customerNumber;

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
}
