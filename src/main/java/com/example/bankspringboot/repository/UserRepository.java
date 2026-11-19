package com.example.bankspringboot.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.bankspringboot.domain.User;

public interface UserRepository extends JpaRepository<User, UUID> {
  @EntityGraph(attributePaths = {
          "roles",
          "roles.permissions"
  })
  Optional<User> findByEmail(String email);
}
