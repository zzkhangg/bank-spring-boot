package com.example.bankspringboot.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleRequest {
  @NotNull(message = "Role name is required.")
  @NotBlank(message = "Role name cannot be blank.")
  String name;

  String description;

  List<String> permissions;
}
