package com.example.bankspringboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableJpaAuditing
@ConfigurationPropertiesScan
//@EnableScheduling
@EnableCaching
public class BankSpringBootApplication {

  public static void main(String[] args) {
    SpringApplication.run(BankSpringBootApplication.class, args);
  }
}
