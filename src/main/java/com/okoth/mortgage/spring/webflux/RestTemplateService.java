package com.okoth.mortgage.spring.webflux;

import com.okoth.mortgage.models.enums.Gateway;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.UnsupportedMediaTypeException;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestTemplateService {
  private final WebClient webClient;
  private final RestLogWriter logWriter;

  /**
   * Executes an HTTP exchange using a WebClient instance with the specified parameters and handles
   * the response. This method supports different HTTP error scenarios and maps them into {@code
   * RestResponseEntity<R>} objects.
   *
   * @param <R> the type of the response body
   * @param <T> the type of the request body
   * @param url the URL to send the request to
   * @param gateway the gateway object used for logging and request context
   * @param method the HTTP method for the request (e.g., GET, POST, DELETE)
   * @param request the HTTP entity containing headers and an optional body to send with the request
   * @param responseType the expected type of the response body
   * @return a {@code Mono<RestResponseEntity<R>>} that emits the response entity wrapping the API
   *     response or error information
   */
  public <R, T> Mono<RestResponseEntity<R>> exchange(
      String url,
      Gateway gateway,
      HttpMethod method,
      HttpEntity<T> request,
      ParameterizedTypeReference<R> responseType) {
    return webClient
        .method(method)
        .uri(url)
        .headers(
            httpHeaders -> {
              HttpHeaders reqHeaders = request.getHeaders();
              if (!reqHeaders.isEmpty()) reqHeaders.forEach(httpHeaders::addAll);
            })
        .body(
            request.getBody() != null
                ? BodyInserters.fromValue(request.getBody())
                : BodyInserters.empty())
        .exchangeToMono(response -> handleResponse(response, responseType))
        .onErrorResume(throwable -> fallBackPlan(url, gateway, request, throwable))
        .timeout(Duration.ofSeconds(15))
        .retryWhen(
            Retry.backoff(3, Duration.ofMillis(200))
                .maxBackoff(Duration.ofSeconds(2))
                .filter(this::isRetriableError)
                .onRetryExhaustedThrow((spec, signal) -> signal.failure()));
  }

  private <T> void log(
      String url,
      Gateway gateway,
      HttpEntity<T> request,
      HttpMethod method,
      HttpStatusCode status,
      String body,
      String error) {
    logWriter.error(url, gateway, request, method, status, body, error);
  }

  // Consolidated error handling with better performance
  private <R, T> Mono<RestResponseEntity<R>> fallBackPlan(
      String url, Gateway gateway, HttpEntity<T> request, Throwable e) {
    String body;
    HttpStatusCode status;

    if (e instanceof WebClientResponseException ex) {
      status = ex.getStatusCode();
      body = ex.getResponseBodyAsString();
    } else if (e instanceof WebClientRequestException) {
      status = HttpStatus.SERVICE_UNAVAILABLE;
      body = e.getMessage();
    } else if (e instanceof UnsupportedMediaTypeException) {
      status = HttpStatus.UNSUPPORTED_MEDIA_TYPE;
      body = e.getMessage();
    } else {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
      body = e.getMessage();
    }
    logAsync(() -> log(url, gateway, request, null, status, body, e.getMessage()));
    return Mono.just(new RestResponseEntity<>(body, status));
  }

  // Async logging to prevent blocking the reactive stream
  private void logAsync(Runnable runnable) {
    CompletableFuture.runAsync(runnable, ForkJoinPool.commonPool())
        .exceptionally(
            throwable -> {
              log.warn("Async logging failed: {}", throwable.getMessage(), throwable);
              return null;
            });
  }

  private <R> Mono<RestResponseEntity<R>> handleResponse(
      ClientResponse response, ParameterizedTypeReference<R> responseType) {
    HttpStatusCode status = response.statusCode();
    HttpHeaders headers = response.headers().asHttpHeaders();
    if (status.is2xxSuccessful())
      return response
          .bodyToMono(responseType)
          .map(r -> new RestResponseEntity<>(new ResponseEntity<>(r, headers, status)))
          .switchIfEmpty(
              Mono.fromSupplier(
                  () -> new RestResponseEntity<>(new ResponseEntity<>(null, headers, status))));
    else
      return response
          .bodyToMono(String.class)
          .defaultIfEmpty("")
          .map(errorBody -> new RestResponseEntity<>(errorBody, status));
  }

  private boolean isRetriableError(Throwable throwable) {
    return throwable instanceof IOException
        || throwable instanceof TimeoutException
        || throwable.getCause() != null && throwable.getCause() instanceof IOException;
  }
}
