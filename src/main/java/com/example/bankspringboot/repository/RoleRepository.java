package com.example.bankspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankspringboot.domain.Role;

public interface RoleRepository extends JpaRepository<Role, String> {}
