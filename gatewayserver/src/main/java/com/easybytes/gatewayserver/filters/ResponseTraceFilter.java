package com.easybytes.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import reactor.core.publisher.Mono;

@Configuration
public class ResponseTraceFilter {

    private static final Logger logger =
            LoggerFactory.getLogger(ResponseTraceFilter.class);

    @Autowired
    FilterUtility filterUtility;

    /**
     * Khai báo GlobalFilter dạng @Bean
     * Đây là POST FILTER → chạy sau khi request đã được xử lý
     */
    @Bean
    public GlobalFilter postGlobalFilter() {

        return (exchange, chain) -> {

            // chain.filter(exchange)
            // -> chuyển request xuống các filter tiếp theo và service
            // .then(...) nghĩa là: khi xử lý xong thì mới chạy đoạn bên trong
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {

                        // Lấy header từ request ban đầu
                        HttpHeaders requestHeaders =
                                exchange.getRequest().getHeaders();

                        // Lấy correlation-id
                        String correlationId =
                                filterUtility.getCorrelationId(requestHeaders);

                        logger.debug(
                                "Updated the correlation id to the outbound headers: {}",
                                correlationId
                        );

                        // Thêm correlation-id vào response header
                        exchange.getResponse()
                                .getHeaders()
                                .add(FilterUtility.CORRELATION_ID,
                                        correlationId);
                    }));
        };
    }
}