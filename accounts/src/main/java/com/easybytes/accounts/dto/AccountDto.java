package com.easybytes.accounts.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Schema(name = "Account", description = "Name of the customer")
@Data
public class AccountDto {
    @Schema(description = "Account Number of EazyBank Account", example = "Easy Bytes")
    @NotEmpty(message = "Account number can not be a null or empty")
    @Pattern(regexp = "(^$|[0-9]{10})", message = "Account number must be 10 digits")
    private Long accountNumber;

    @Schema(description = "Account type of EazyBank Aaccount", example = "Savings")
    @NotEmpty(message = "Account type address can not be a null or empty")
    private String accountType;

    @Schema(description = "EasyBank branch address")
    @NotEmpty(message = "Branch address can not be a null or empty")
    private String branchAddress;
}
