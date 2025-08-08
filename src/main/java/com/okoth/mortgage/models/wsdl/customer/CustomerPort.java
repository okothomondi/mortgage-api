package com.okoth.mortgage.models.wsdl.customer;

import com.okoth.mortgage.models.wsdl.ObjectFactory;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.xml.bind.annotation.XmlSeeAlso;

@WebService(name = "CustomerPort", targetNamespace = "http://credable.io/cbs/customer")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({ObjectFactory.class})
public interface CustomerPort {
  /**
   * @param customerRequest CustomerRequest
   * @return returns com.okoth.credable.stubs.CustomerResponse
   */
  @WebMethod(operationName = "Customer")
  @WebResult(
      name = "CustomerResponse",
      targetNamespace = "http://credable.io/cbs/customer",
      partName = "CustomerResponse")
  CustomerResponse customer(
      @WebParam(
              name = "CustomerRequest",
              targetNamespace = "http://credable.io/cbs/customer",
              partName = "CustomerRequest")
          CustomerRequest customerRequest);
}
