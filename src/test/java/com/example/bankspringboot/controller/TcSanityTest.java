package com.example.bankspringboot.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class TcSanityTest {
  @Container static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

  @Test
  void containerStarts() {
    assertTrue(mysql.isRunning());
  }
}
