package com.okoth.mortgage.spring.webflux;

import com.okoth.mortgage.models.enums.Gateway;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestLogWriter {

  private final ExecutorService executorService = Executors.newCachedThreadPool();

  public <T, R> void warn(
      String url,
      Gateway gateway,
      HttpEntity<T> entity,
      HttpMethod method,
      ResponseEntity<R> response) {
    executorService.submit(
        () ->
            log.warn(
                new Logger(url, gateway, entity.getBody(), method, response.getBody()).toString()));
  }

  public <T, R> void info(
      String url,
      Gateway gateway,
      HttpEntity<T> entity,
      HttpMethod method,
      ResponseEntity<R> response) {
    executorService.submit(
        () ->
            log.info(
                new Logger(url, gateway, entity.getBody(), method, response.getBody()).toString()));
  }

  public <T> void error(
      String url,
      Gateway gateway,
      HttpEntity<T> entity,
      HttpMethod method,
      HttpStatusCode code,
      String response,
      String error) {
    executorService.submit(
        () ->
            log.error(
                new Logger(url, gateway, entity.getBody(), method, code, response, error)
                    .toString()));
  }

  public void shutdown() {
    executorService.shutdown();
  }
}
