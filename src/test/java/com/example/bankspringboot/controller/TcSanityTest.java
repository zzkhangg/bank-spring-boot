package com.example.bankspringboot.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public class TcSanityTest {

  @Container
  static final MySQLContainer<?> mysql =
      new MySQLContainer<>("mysql:8.3")
          .withDatabaseName("bank")
          .withUsername("test")
          .withPassword("test");

  @Test
  void containerStarts() {
    assertTrue(mysql.isRunning());
    System.out.println(mysql.getJdbcUrl());
  }
}
