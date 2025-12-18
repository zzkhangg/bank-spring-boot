package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.role.RoleRequest;
import com.example.bankspringboot.dto.role.RoleResponse;
import com.example.bankspringboot.service.RoleService;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {
  RoleService roleService;

  @GetMapping
  public List<RoleResponse> getRoles() {
    return roleService.getRoles();
  }

  @GetMapping("/{id}")
  public RoleResponse getRole(@PathVariable(name = "id") String id) {
    return roleService.getRole(id);
  }

  @PostMapping
  public RoleResponse createRole(@RequestBody RoleRequest roleRequest) {
    return roleService.createRole(roleRequest);
  }

  @DeleteMapping("/{id}")
  public void deleteRole(@PathVariable(name = "id") String id) {
    roleService.deleteRole(id);
  }

  @PutMapping("/{id}")
  public RoleResponse updateRole(@PathVariable(name = "id") String id, @RequestBody RoleRequest roleRequest) {
    return roleService.updateRole(id, roleRequest);
  }
}
