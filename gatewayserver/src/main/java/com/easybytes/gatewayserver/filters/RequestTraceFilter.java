package com.easybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * GlobalFilter: filter áp dụng cho TẤT CẢ request đi qua Gateway
 *
 * @Order(1): xác định thứ tự chạy filter (số nhỏ chạy trước)
 */
@Order(1)
@Component
public class RequestTraceFilter implements GlobalFilter {

    // Logger để debug
    private static final Logger logger =
            LoggerFactory.getLogger(RequestTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;  // class hỗ trợ lấy/set correlation-id

    /**
     * Method chính của GlobalFilter
     * exchange: chứa toàn bộ thông tin HTTP request + response
     * chain: chuỗi các filter tiếp theo
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        // Lấy toàn bộ header của request
        HttpHeaders requestHeaders =
                exchange.getRequest().getHeaders();

        // Kiểm tra request đã có correlation-id chưa
        if (isCorrelationIdPresent(requestHeaders)) {

            // Nếu có rồi → chỉ log ra
            logger.debug(
                    "easybank-correlation-id found in RequestTraceFilter : {}",
                    filterUtility.getCorrelationId(requestHeaders)
            );

        } else {

            // Nếu chưa có → tạo mới
            String correlationID = generateCorrelationId();

            // Gắn correlation-id vào request
            exchange = filterUtility.setCorrelationId(exchange, correlationID);

            logger.debug(
                    "easybank-correlation-id generated in RequestTraceFilter : {}",
                    correlationID
            );
        }

        // Chuyển request sang filter tiếp theo
        return chain.filter(exchange);
    }

    /**
     * Kiểm tra header có correlation-id không
     */
    private boolean isCorrelationIdPresent(HttpHeaders requestHeaders) {
        return filterUtility.getCorrelationId(requestHeaders) != null;
    }

    /**
     * Tạo correlation-id ngẫu nhiên
     * UUID đảm bảo unique cho mỗi request
     */
    private String generateCorrelationId() {
        return java.util.UUID.randomUUID().toString();
    }
}