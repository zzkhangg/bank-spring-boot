package com.example.bankspringboot.repository;

import com.example.bankspringboot.domain.admin.Admin;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRespository extends JpaRepository<Admin, UUID> {
  Optional<Admin> findByUsername(String username);
}
