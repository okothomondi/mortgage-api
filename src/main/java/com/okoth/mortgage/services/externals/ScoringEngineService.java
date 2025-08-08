package com.okoth.mortgage.services.externals;

import static com.okoth.mortgage.models.enums.Gateway.SCORING_API;
import static com.okoth.mortgage.spring.webflux.HttpHeadersUtil.httpHeaders;

import com.okoth.mortgage.models.custom.CreateCustomer;
import com.okoth.mortgage.models.custom.CustomerScore;
import com.okoth.mortgage.spring.webflux.RestResponseEntity;
import com.okoth.mortgage.spring.webflux.RestTemplateService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScoringEngineService {
  private @Value("${app.scoring.token:token}") String token;
  private @Value("${app.scoring.url:plocalhost}") String url;

  private final RestTemplateService restTemplateService;

  @Retryable(
      maxAttempts = 5,
      retryFor = {RestClientException.class, NullPointerException.class},
      backoff = @Backoff(delay = 3000, multiplier = 1.5))
  public Mono<CustomerScore> getScore(String token) {
    return restTemplateService
        .exchange(
            url.concat("/scoring/queryScore/").concat(token),
            SCORING_API,
            HttpMethod.GET,
            new HttpEntity<>(null, httpHeaders()),
            new ParameterizedTypeReference<CustomerScore>() {})
        .map(RestResponseEntity::getBody)
        .onErrorResume(
            throwable -> {
              log.error("Throwable in getScore({}): {}", token, throwable.getMessage());
              return Mono.just(new CustomerScore());
            });
  }

  public Mono<CreateCustomer> postCustomer(CreateCustomer createCustomer) {
    return restTemplateService
        .exchange(
            url.concat("/client/createClient"),
            SCORING_API,
            HttpMethod.POST,
            new HttpEntity<>(createCustomer, httpHeaders()),
            new ParameterizedTypeReference<CreateCustomer>() {})
        .map(RestResponseEntity::getBody)
        .onErrorResume(
            throwable -> {
              log.error("Throwable in postCustomer({}): {}", token, throwable.getMessage());
              return Mono.just(new CreateCustomer());
            });
  }

  @Recover
  public CustomerScore recoverAfterRetries(Exception e, String token) {
    log.warn("Max retries exhausted for token: {}. Reason: {}", token, e.getMessage());
    return null; // Or throw LoanException
  }

  private HttpHeaders httpHeaders() {
    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.add("client-token", token);
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
    httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
    return httpHeaders;
  }
}
