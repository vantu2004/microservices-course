package com.easybytes.accounts.function;

import com.easybytes.accounts.service.IAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class AccountsFuction {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountsFuction.class);

    @Bean
    public Consumer<Long> updateCommunication(IAccountService iAccountService){
        return accountNumber -> {
            LOGGER.info("Updating Communication status for the account number: {}", accountNumber.toString());

            iAccountService.updateCommunicationStatus(accountNumber);
        };
    }

}
