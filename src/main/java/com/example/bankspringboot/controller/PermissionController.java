package com.example.bankspringboot.controller;

import com.example.bankspringboot.dto.permission.PermissionDTO;
import com.example.bankspringboot.service.PermissionService;
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
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

  PermissionService permissionService;

  @GetMapping
  public List<PermissionDTO> getPermissions() {
    return permissionService.getPermissions();
  }

  @GetMapping("/{id}")
  public PermissionDTO getPermission(@PathVariable String id) {
    return permissionService.getPermission(id);
  }

  @PostMapping
  public PermissionDTO createPermission(@RequestBody PermissionDTO dto) {
    return permissionService.createPermission(dto);
  }

  @DeleteMapping("/{id}")
  public void deletePermission(@PathVariable String id) {
    permissionService.deletePermission(id);
  }

  @PutMapping("/{id}")
  public PermissionDTO updatePermission(@RequestBody PermissionDTO dto,
      @PathVariable("id") String id) {
    return permissionService.updatePermission(id, dto);
  }
}
