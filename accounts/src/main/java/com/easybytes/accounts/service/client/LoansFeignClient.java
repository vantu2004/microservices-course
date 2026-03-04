package com.easybytes.accounts.service.client;

import com.easybytes.accounts.dto.LoansDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// dùng đúng tên đã đăng ký trong eureka server
@FeignClient(name = "loans", fallback = LoansFallback.class)
public interface LoansFeignClient {
    @GetMapping(value = "/api/loan", consumes = "application/json")
    ResponseEntity<LoansDto> fetchLoanDetails(@RequestHeader("easybank-correlation-id") String correlationId, @RequestParam String mobileNumber);
}
