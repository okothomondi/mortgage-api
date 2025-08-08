package com.okoth.mortgage.models.wsdl.customer;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"customer"})
@XmlRootElement(name = "CustomerResponse")
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

  @XmlElement(required = true)
  protected Customer customer;
}
