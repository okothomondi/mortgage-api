package com.okoth.mortgage.models.wsdl.customer;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"customerNumber"})
@XmlRootElement(name = "CustomerRequest")
public class CustomerRequest {

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
