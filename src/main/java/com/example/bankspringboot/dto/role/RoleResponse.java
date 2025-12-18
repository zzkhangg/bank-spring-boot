package com.example.bankspringboot.dto.role;

import com.example.bankspringboot.dto.permission.PermissionDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse {
  @NotNull
  @NotBlank
  String name;

  String description;

  List<PermissionDTO> permissions;
}
