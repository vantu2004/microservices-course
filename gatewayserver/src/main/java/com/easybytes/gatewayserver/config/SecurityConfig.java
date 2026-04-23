package com.easybytes.gatewayserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.EnableWebFlux;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        serverHttpSecurity.authorizeExchange(exchanges -> exchanges
                        .pathMatchers(HttpMethod.GET).permitAll()
                        // Spring tự hiểu hasRole tự thêm ROLE_ vào và hiểu dưới dạng hasAuthority(ROLE_ACCOUNTS)
                        // hasAuthority Spring hiểu đúng chuỗi truyền vào ko thêm bớt
                        .pathMatchers("/easybank/accounts/**").hasRole("ACCOUNTS")
                        .pathMatchers("/easybank/loans/**").hasRole("LOANS")
                        .pathMatchers("/easybank/cards/**").hasRole("CARDS"))
                .oauth2ResourceServer(oAuth2ResourceServerSpec -> oAuth2ResourceServerSpec
                        .jwt(jwtSpec -> jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor())));

        serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable());

        return serverHttpSecurity.build();
    }

    // Method này trả về 1 Converter dùng cho Spring Security (Reactive), convert từ Jwt → Authentication (có chứa roles/authorities)
    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {

        // Converter mặc định của Spring Security dùng để chuyển Jwt → JwtAuthenticationToken
        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();

        // Set custom converter để extract roles từ JWT của Keycloak vì mặc định Spring KHÔNG đọc được "realm_access.roles"
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                new KeycloakRoleConverter()
        );

        // Adapter dùng để chuyển từ blocking (sync) → reactive (Mono) vì Gateway/WebFlux yêu cầu reactive type
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}
