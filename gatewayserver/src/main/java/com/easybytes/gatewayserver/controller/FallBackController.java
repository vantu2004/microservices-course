package com.easybytes.gatewayserver.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/fall-back")
public class FallBackController {
    @GetMapping("/contact-support")
    public Mono<String> contactSupport() {
        return Mono.just("An error occurred.Please try after some time ỏ contact support team");
    }
}
