package com.easybytes.accounts.controller;

import com.easybytes.accounts.dto.CustomerDetailsDto;
import com.easybytes.accounts.dto.ErrorResponseDto;
import com.easybytes.accounts.service.ICustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "REST APIs for Customers in EasyBank",
        description = "REST APIs in EasyBank to FETCH customer details")
@RestController
@AllArgsConstructor
@Validated
@RequestMapping(path = "/api/customer", produces = {MediaType.APPLICATION_JSON_VALUE})
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private ICustomerService iCustomerService;

    @Operation(
            summary = "Fetch Customer Details REST API",
            description = "REST API to fetch Customer details based on a mobile number"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping
    public ResponseEntity<CustomerDetailsDto> fetchCustomerDetails(@RequestHeader("easybank-correlation-id") String correlationId, @RequestParam @Pattern(regexp = "(^$|[0-9]{10})", message = "Mobile number must be 10 digits") String mobileNumber) {
//        logger.debug("easybank-correlation-id found: {}", correlationId);

        logger.debug("fetchCustomerDetais method start");
        CustomerDetailsDto customerDetailsDto = iCustomerService.fetchCustomerDetails(correlationId, mobileNumber);
        logger.debug("fetchCustomerDetais method end");

        return ResponseEntity.ok(customerDetailsDto);
    }
}
