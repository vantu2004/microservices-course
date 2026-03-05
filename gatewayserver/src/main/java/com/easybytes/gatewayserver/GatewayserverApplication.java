package com.easybytes.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.joda.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

import java.time.Duration;

@SpringBootApplication
public class GatewayserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayserverApplication.class, args);
    }

    // tự define bằng RouteLocatorBuilder
    @Bean
    public RouteLocator easybankRouteConfig(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p
                        // điều kiện match url
                        .path("/easybank/accounts/**")
                        .filters(f -> f
                                // cắt bỏ /easybank/accounts, gửi segment (phần đuôi, vd: /api/account) đến ACCOUNTS service
                                .rewritePath(
                                        "/easybank/accounts/(?<segment>.*)",
                                        "/${segment}"
                                )
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                // cấu hình ở đây là bảo vệ request từ client-microservice
                                // cấu hình ở riêng service là bảo vệ service-to-service call
                                .circuitBreaker(config -> config
                                        // truy cập /actuator/circuitbreakerevents?name=accountsCircuitBreaker
                                        .setName("accountsCircuitBreaker")
                                        .setFallbackUri("forward:/api/v1/fall-back/contact-support"))
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(p -> p
                        .path("/easybank/loans/**")
                        .filters(f -> f
                                .rewritePath(
                                        "/easybank/loans/(?<segment>.*)",
                                        "/${segment}"
                                )
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())

                                // ko thử circuitbreaker nữa mà chuyển qua http-timeout bên application.yml

                                .retry(config -> config
                                        .setRetries(3)
                                        .setMethods(HttpMethod.GET)
                                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000), 2, true)
                                )
                        )
                        .uri("lb://LOANS")
                )
                .route(p -> p
                        .path("/easybank/cards/**")
                        .filters(f -> f
                                .rewritePath(
                                        "/easybank/cards/(?<segment>.*)",
                                        "/${segment}"
                                )
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                                .circuitBreaker(config -> config
                                        .setName("cardsCircuitBreaker")
                                        .setFallbackUri("forward:/api/v1/fall-back/contact-support"))
                        )
                        .uri("lb://CARDS")
                )
                .build();
    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id ->
                new Resilience4JConfigBuilder(id)
                        .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
                        .timeLimiterConfig(
                                TimeLimiterConfig.custom()
                                        .timeoutDuration(Duration.ofSeconds(4))
                                        .build()
                        )
                        .build()
        );
    }
}
