package com.example.bankspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankspringboot.domain.alert.Alert;

public interface AlertRepository extends JpaRepository<Alert, Long> {}
