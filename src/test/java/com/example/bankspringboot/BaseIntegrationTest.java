package com.example.bankspringboot;

import com.example.bankspringboot.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
// @DataJpaTest only auto-enables schema generation for embedded databases. Against the
// MySQL container it defaults to "none", so the schema has to be requested explicitly.
@TestPropertySource(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
public class BaseIntegrationTest {

  @Container
  @ServiceConnection
  static final MySQLContainer<?> mysql =
      new MySQLContainer<>("mysql:8.3")
          .withDatabaseName("bank")
          .withUsername("test")
          .withPassword("test");

  @Autowired AccountRepository accountRepository;

  @Autowired TestEntityManager entityManager;
}
