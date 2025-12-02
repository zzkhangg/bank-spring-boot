package com.example.bankspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
@RestController
public class BankSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankSpringBootApplication.class, args);
    }
}