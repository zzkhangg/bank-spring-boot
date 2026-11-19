package com.example.bankspringboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankspringboot.domain.Permission;

public interface PermissionRepository extends JpaRepository<Permission, String> {}
