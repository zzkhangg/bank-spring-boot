package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.Permission;
import com.example.bankspringboot.domain.Role;
import com.example.bankspringboot.dto.role.RoleRequest;
import com.example.bankspringboot.dto.role.RoleResponse;
import com.example.bankspringboot.mapper.PermissionMapper;
import com.example.bankspringboot.mapper.RoleMapper;
import com.example.bankspringboot.repository.PermissionRepository;
import com.example.bankspringboot.repository.RoleRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {

  RoleRepository roleRepository;
  PermissionRepository permissionRepository;
  RoleMapper roleMapper;
  private final PermissionMapper permissionMapper;

  public RoleResponse createRole(RoleRequest request) {
    if (roleRepository.existsById(request.getName())) {
      throw new RuntimeException("Role with name " + request.getName() + " already exists");
    }
    Role role = roleMapper.toRole(request);
    List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));
    return roleMapper.toResponse(roleRepository.save(role));
  }

  public RoleResponse updateRole(String id, RoleRequest request) {
    Role role = roleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Role with id " + id + " not found"));
    role.setName(request.getName());
    List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
    role.setPermissions(new HashSet<>(permissions));
    return roleMapper.toResponse(roleRepository.save(role));
  }

  public void deleteRole(String id) {
    Role role = roleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Role with id " + id + " not found"));
    roleRepository.delete(role);
  }

  public RoleResponse getRole(String id) {
    Role role = roleRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Role with id " + id + " not found"));
    return roleMapper.toResponse(role);
  }

  public List<RoleResponse> getRoles() {
    return roleRepository.findAll().stream().map(roleMapper::toResponse).toList();
  }
}
