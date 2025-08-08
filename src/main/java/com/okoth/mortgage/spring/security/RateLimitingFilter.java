package com.okoth.mortgage.spring.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.okoth.mortgage.exceptions.ErrorResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.joda.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

  private static final int REQUESTS_PER_MINUTE = 100;
  private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String clientIp = getClientIp(request);
    Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createNewBucket());

    ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
    if (probe.isConsumed()) {
      response.addHeader("X-Rate-Limit-Remaining", String.valueOf(probe.getRemainingTokens()));
      filterChain.doFilter(request, response);
    } else {
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.addHeader(
          "X-Rate-Limit-Retry-After-Seconds",
          String.valueOf(TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())));

      ErrorResponse errorResponse =
          new ErrorResponse(
              Instant.now().toString(),
              HttpStatus.TOO_MANY_REQUESTS.value(),
              HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
              "Too many requests - please try again later",
              request.getRequestURI());

      response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
  }

  private Bucket createNewBucket() {
    return Bucket.builder()
        .addLimit(
            Bandwidth.classic(
                REQUESTS_PER_MINUTE, Refill.intervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1))))
        .build();
  }

  private String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("X-Forwarded-For");
    if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }
}
