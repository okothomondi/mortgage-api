package com.okoth.mortgage.models.wsdl.enums;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Java class for idType.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <p>
 *
 * <pre>
 * &lt;simpleType name="idType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="PASSPORT"/&gt;
 *     &lt;enumeration value="NATIONAL_ID"/&gt;
 *     &lt;enumeration value="DRIVERS_LICENSE"/&gt;
 *     &lt;enumeration value="VOTERS_ID"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 */
@XmlType(name = "idType")
@XmlEnum
public enum IdType {
  PASSPORT,
  NATIONAL_ID,
  DRIVERS_LICENSE,
  VOTERS_ID;

  public String value() {
    return name();
  }

  public static IdType fromValue(String v) {
    return valueOf(v);
  }
}
