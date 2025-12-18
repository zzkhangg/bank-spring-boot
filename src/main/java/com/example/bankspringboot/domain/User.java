package com.example.bankspringboot.domain;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name= "users",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
@DiscriminatorColumn(name = "user_type")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  UUID id;

  String email;
  String password;

  @ManyToMany
  Set<Role> roles;
}
