package com.example.bankspringboot.domain.admin;

import com.example.bankspringboot.common.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.UuidGenerator;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(
    name= "admins",
    uniqueConstraints = @UniqueConstraint(columnNames = "username")
)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {
  @Id
  @UuidGenerator
  UUID id;

  String username;
  String password;

  @Enumerated(EnumType.STRING)
  Set<Role> roles;
}
