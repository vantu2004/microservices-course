package com.easybytes.accounts.service.client;

import com.easybytes.accounts.dto.CardsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// dùng đúng tên đã đăng ký trong eureka server
@FeignClient(name = "cards", fallback = CardsFallback.class)
public interface CardsFeignClient {
    @GetMapping(value = "/api/card", consumes = "application/json")
    ResponseEntity<CardsDto> fetchCardDetails(@RequestHeader("easybank-correlation-id") String correlationId, @RequestParam String mobileNumber);
}
