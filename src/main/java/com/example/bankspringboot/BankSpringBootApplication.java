package com.example.bankspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableJpaAuditing
public class BankSpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(BankSpringBootApplication.class, args);
  }
}
