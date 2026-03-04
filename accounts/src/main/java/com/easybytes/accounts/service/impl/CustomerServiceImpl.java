package com.easybytes.accounts.service.impl;

import com.easybytes.accounts.dto.AccountDto;
import com.easybytes.accounts.dto.CardsDto;
import com.easybytes.accounts.dto.CustomerDetailsDto;
import com.easybytes.accounts.dto.LoansDto;
import com.easybytes.accounts.entity.Account;
import com.easybytes.accounts.entity.Customer;
import com.easybytes.accounts.exception.ResourceNotFoundException;
import com.easybytes.accounts.mapper.AccountMapper;
import com.easybytes.accounts.mapper.CustomerMapper;
import com.easybytes.accounts.repository.AccountRepository;
import com.easybytes.accounts.repository.CustomerRepository;
import com.easybytes.accounts.service.ICustomerService;
import com.easybytes.accounts.service.client.CardsFeignClient;
import com.easybytes.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {
    private AccountRepository accountRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String correlationId, String mobileNumber) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(() -> new ResourceNotFoundException
                ("Customer", "mobileNumber", mobileNumber));

        Account account = accountRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(() -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountDto(AccountMapper.mapToAccountDto(account, new AccountDto()));

        ResponseEntity<LoansDto> loansResponse = loansFeignClient.fetchLoanDetails(correlationId, mobileNumber);
        if (loansResponse != null) {
            customerDetailsDto.setLoansDto(loansResponse.getBody());
        }

        ResponseEntity<CardsDto> cardsResponse = cardsFeignClient.fetchCardDetails(correlationId, mobileNumber);
        if (cardsResponse != null) {
            customerDetailsDto.setCardsDto(cardsResponse.getBody());
        }

        return customerDetailsDto;
    }
}
