package com.okoth.mortgage.exceptions;

public record ErrorResponse(
    String timestamp, int status, String error, String message, String path) {
  // No need for additional methods - record provides everything
}
