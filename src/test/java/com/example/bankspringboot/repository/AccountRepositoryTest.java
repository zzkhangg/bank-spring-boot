package com.example.bankspringboot.repository;

import com.example.bankspringboot.common.CustomerTypeCode;
import com.example.bankspringboot.domain.account.Account;
import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.customer.Customer;
import com.example.bankspringboot.domain.customer.CustomerType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerTypeRepository customerTypeRepository;

    @Test
    void customerCanOwnMultipleAccounts() {

        CustomerType customerType = customerTypeRepository.save(CustomerType.builder().code(CustomerTypeCode.INDIVIDUAL).maxTransactionLimit(BigDecimal.valueOf(50_000)).build());

        Customer customer = customerRepository.save(
                Customer.builder()
                        .email("john@test.com")
                        .password("password123")
                        .firstName("Khang")
                        .lastName("Vuong")
                        .phoneNumber("0434871348")
                        .birthDate(LocalDate.now())
                        .address(Address.builder().city("HCM").country("VN").street("Van Coi Street").postalCode("70000").build())
                        .customerType(customerType)
                        .build()
        );

        Account account1 = Account.builder()
                .accountNumber("1111111111")
                .customer(customer)
                .build();

        Account account2 = Account.builder()
                .accountNumber("2222222222")
                .customer(customer)
                .build();

        accountRepository.save(account1);
        accountRepository.save(account2);

        List<Account> accounts =
                accountRepository.findByCustomer_Id(customer.getId());

        assertThat(accounts).hasSize(2);
    }
}