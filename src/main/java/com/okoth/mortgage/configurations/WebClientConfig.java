package com.okoth.mortgage.configurations;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Component
public class WebClientConfig {

  @Bean
  public WebClient webClient() {
    return WebClient.builder()
        .clientConnector(
            new ReactorClientHttpConnector(
                HttpClient.create(
                        ConnectionProvider.builder("custom")
                            .maxConnections(500)
                            .maxIdleTime(Duration.ofSeconds(30))
                            .maxLifeTime(Duration.ofMinutes(10))
                            .pendingAcquireTimeout(Duration.ofSeconds(10))
                            .evictInBackground(Duration.ofSeconds(120))
                            .build())
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .responseTimeout(Duration.ofSeconds(10))
                    .compress(true)
                    .keepAlive(true)
                    .doOnConnected(
                        connection ->
                            connection
                                .addHandlerLast(new ReadTimeoutHandler(10, TimeUnit.SECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(10, TimeUnit.SECONDS)))))
        .codecs(c -> c.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
        .build();
  }

  @Bean
  public ReactorResourceFactory reactorResourceFactory() {
    ReactorResourceFactory factory = new ReactorResourceFactory();
    factory.setUseGlobalResources(false);
    factory.setConnectionProvider(
        ConnectionProvider.builder("http-broker")
            .maxConnections(1000)
            .maxIdleTime(Duration.ofSeconds(30))
            .maxLifeTime(Duration.ofMinutes(10))
            .pendingAcquireTimeout(Duration.ofSeconds(5))
            .evictInBackground(Duration.ofSeconds(60))
            .build());
    return factory;
  }
}
