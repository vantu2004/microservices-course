package com.easybytes.gatewayserver.filters;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.http.HttpHeaders;

import java.util.List;

@Component
public class FilterUtility {

    // Tên header dùng để trace request xuyên hệ thống
    public static final String CORRELATION_ID = "easybank-correlation-id";

    /**
     * Lấy correlation-id từ request header
     */
    public String getCorrelationId(HttpHeaders requestHeaders) {

        // Nếu header tồn tại
        if (requestHeaders.get(CORRELATION_ID) != null) {

            // Vì header có thể có nhiều giá trị → trả về List<String>
            List<String> requestHeaderList =
                    requestHeaders.get(CORRELATION_ID);

            // Lấy giá trị đầu tiên
            return requestHeaderList.stream().findFirst().get();

        } else {
            return null;
        }
    }

    /**
     * Set header mới vào request (Reactive nên không sửa trực tiếp được)
     */
    public ServerWebExchange setRequestHeader(ServerWebExchange exchange,
                                              String name,
                                              String value) {

        return exchange.mutate()                  // Tạo exchange mới
                .request(
                        exchange.getRequest()
                                .mutate()        // Tạo request mới
                                .header(name, value) // Thêm header
                                .build()
                )
                .build();
    }

    /**
     * Set correlation-id vào request
     */
    public ServerWebExchange setCorrelationId(ServerWebExchange exchange,
                                              String correlationId) {
        return this.setRequestHeader(exchange,
                CORRELATION_ID,
                correlationId);
    }
}