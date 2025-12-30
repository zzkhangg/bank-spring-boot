package com.example.bankspringboot.config;

import com.example.bankspringboot.domain.admin.Admin;
import com.example.bankspringboot.repository.AdminRespository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

  @NonFinal
  static final String ADMIN_USER_NAME = "admin";

  @NonFinal
  static final String ADMIN_PASSWORD = "admin";
    @Bean
    ApplicationRunner applicationRunner(AdminRespository adminRespository,
        PasswordEncoder passwordEncoder) {
        return args -> {
          if (adminRespository.findByEmail(ApplicationInitConfig.ADMIN_USER_NAME).isEmpty()) {

            adminRespository.save(Admin.builder()
                    .email(ApplicationInitConfig.ADMIN_USER_NAME)
                    .password(passwordEncoder.encode(ApplicationInitConfig.ADMIN_PASSWORD))
//                    .roles()
                .build());
            log.warn("admin user has been created with default password: admin, please change it");
          }
          log.info("Application initialization completed .....");
        };
    }
}
