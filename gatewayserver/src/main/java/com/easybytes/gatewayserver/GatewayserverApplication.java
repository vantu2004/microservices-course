package com.easybytes.gatewayserver;

import org.joda.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
                                ).addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                        )
                        .uri("lb://ACCOUNTS")
                )
                .route(p -> p
                        .path("/easybank/loans/**")
                        .filters(f -> f
                                .rewritePath(
                                        "/easybank/loans/(?<segment>.*)",
                                        "/${segment}"
                                ).addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                        )
                        .uri("lb://LOANS")
                )
                .route(p -> p
                        .path("/easybank/cards/**")
                        .filters(f -> f
                                .rewritePath(
                                        "/easybank/cards/(?<segment>.*)",
                                        "/${segment}"
                                ).addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                        )
                        .uri("lb://CARDS")
                )
                .build();
    }

}
