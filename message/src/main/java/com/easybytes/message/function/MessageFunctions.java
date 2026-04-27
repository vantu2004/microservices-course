package com.easybytes.message.function;

import com.easybytes.message.dto.AccountsMsgDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {
    private static final Logger LOGGER = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<AccountsMsgDto, AccountsMsgDto> email() {
        return accountsMsgDto -> {
            LOGGER.info("Sending email with the details: " + accountsMsgDto.toString());

            return accountsMsgDto;
        };
    }
    @Bean
    public Function<AccountsMsgDto, Long> sms() {
        return accountsMsgDto -> {
            LOGGER.info("Sending sms with the details: " + accountsMsgDto.toString());

            return accountsMsgDto.accountNumber();
        };
    }
}
