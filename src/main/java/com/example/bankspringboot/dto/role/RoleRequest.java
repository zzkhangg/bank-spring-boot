package com.example.bankspringboot.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleRequest {
  @NotNull @NotBlank String name;

  String description;

  List<String> permissions;
}
