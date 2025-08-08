package com.okoth.mortgage.configurations;

import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OtlpConfig {
  @Bean
  OtlpGrpcSpanExporter otlpGrpcSpanExporter(
      @Value("${opentelemetry.exporter.otlp.endpoint}") String endpoint) {
    return OtlpGrpcSpanExporter.builder()
        .setEndpoint(endpoint)
        .setTimeout(30, TimeUnit.SECONDS)
        .build();
  }
}
