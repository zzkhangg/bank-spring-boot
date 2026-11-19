package com.example.bankspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankspringboot.domain.InvalidatedToken;

public interface InvalidatedTokenRepository extends JpaRepository<InvalidatedToken, String> {}
