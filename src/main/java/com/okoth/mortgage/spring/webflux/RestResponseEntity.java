package com.okoth.mortgage.spring.webflux;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RestResponseEntity<R> {
  private R body;
  private String message;
  private HttpStatusCode statusCode;

  public RestResponseEntity(String message, HttpStatusCode statusCode) {
    this.body = null;
    this.message = message;
    this.statusCode = statusCode;
  }

  public RestResponseEntity(ResponseEntity<R> responseEntity) {
    this.body = responseEntity.getBody();
    this.message = responseEntity.toString();
    this.statusCode = responseEntity.getStatusCode();
  }
}
