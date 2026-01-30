package com.easybytes.eurekaserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping(path = "/api/eureka")
// phải dùng tên khác vì Spring Cloud sẽ tự động tạo sẵn một controller nội bộ tên là eurekaController
public class CustomEurekaController {
    // các service khác dùng @ConfigurationProperties nên nó tự refresh được nên lấy lại giá trị mới, còn @Value chỉ inject khi start server, phải dùng @RefreshScope để lấy giá trị mới
    @Value("${additional.description}")
    private String eurekaDescription;

    @GetMapping("/description")
    public ResponseEntity<String> getEurekaDescription(){
        return ResponseEntity.ok(eurekaDescription);
    }
}
