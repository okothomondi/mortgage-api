package com.okoth.mortgage.models.wsdl.customer;

import com.okoth.mortgage.models.dto.UserDTO;
import com.okoth.mortgage.models.wsdl.enums.Gender;
import com.okoth.mortgage.models.wsdl.enums.IdType;
import com.okoth.mortgage.models.wsdl.enums.Status;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "customer",
    propOrder = {
      "createdAt",
      "createdDate",
      "customerNumber",
      "dob",
      "email",
      "firstName",
      "gender",
      "id",
      "idNumber",
      "idType",
      "lastName",
      "middleName",
      "mobile",
      "monthlyIncome",
      "status",
      "updatedAt"
    })
@NoArgsConstructor
public class Customer {

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar createdAt;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar createdDate;

  protected String customerNumber;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar dob;

  protected String email;
  protected String firstName;

  @XmlSchemaType(name = "string")
  protected Gender gender;

  protected Long id;
  protected String idNumber;

  @XmlSchemaType(name = "string")
  protected IdType idType;

  protected String lastName;
  protected String middleName;
  protected String mobile;
  protected double monthlyIncome;

  @XmlSchemaType(name = "string")
  protected Status status;

  @XmlSchemaType(name = "dateTime")
  protected XMLGregorianCalendar updatedAt;

  public Customer(UserDTO user) {
    this.idNumber = user.getNationalId();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.email = user.getEmail();
    this.status = Status.INACTIVE;
  }
}
