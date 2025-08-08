package com.okoth.mortgage.models.custom;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

@Data
@ToString
@NoArgsConstructor
public class RestResponseEntity<R> {
  private R body;
  String message;
  private HttpStatusCode statusCode;

  public RestResponseEntity(String message, HttpStatusCode statusCode) {
    this.body = null;
    this.message = message;
    this.statusCode = statusCode;
  }

  public RestResponseEntity(ResponseEntity<R> responseEntity, HttpStatusCode statusCode) {
    this.statusCode = statusCode;
    this.body = responseEntity.getBody();
    this.message = responseEntity.toString();
  }
}
