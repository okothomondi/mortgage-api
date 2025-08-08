package com.okoth.mortgage.spring.webflux;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.okoth.mortgage.models.enums.Gateway;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Logger {
  String url;
  Gateway gateway;
  Object httpEntity;
  String httpMethod;
  Object responseEntity;
  HttpStatusCode httpStatusCode;
  String response;
  String error;

  public Logger(
      String url,
      Gateway gateway,
      Object httpEntity,
      HttpMethod httpMethod,
      Object responseEntity) {
    this.url = url;
    this.gateway = gateway;
    this.httpEntity = httpEntity;
    this.httpMethod = httpMethod.name();
    this.responseEntity = responseEntity;
  }

  public Logger(
      String url,
      Gateway gateway,
      Object httpEntity,
      HttpMethod httpMethod,
      HttpStatusCode httpStatusCode,
      String response,
      String error) {
    this.url = url;
    this.gateway = gateway;
    this.httpEntity = httpEntity;
    this.httpMethod = httpMethod.name();
    this.httpStatusCode = httpStatusCode;
    this.response = response;
    this.error = error;
  }

  @Override
  public String toString() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      return "Error serializing Logger toString(): " + e.getMessage();
    }
  }
}
