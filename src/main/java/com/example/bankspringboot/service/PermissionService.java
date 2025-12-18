package com.example.bankspringboot.service;

import com.example.bankspringboot.domain.Permission;
import com.example.bankspringboot.dto.permission.PermissionDTO;
import com.example.bankspringboot.exceptions.BusinessException;
import com.example.bankspringboot.mapper.PermissionMapper;
import com.example.bankspringboot.repository.PermissionRepository;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionService {

  PermissionMapper permissionMapper;
  PermissionRepository permissionRepository;

  @Transactional
  public PermissionDTO createPermission(PermissionDTO request) {
    if (permissionRepository.existsById(request.getName())) {
      throw new BusinessException("Permission with name " + request.getName() + " already exists");
    }
    return permissionMapper.toDTO(
        permissionRepository.save(permissionMapper.toPermission(request)));
  }

  @Transactional
  public PermissionDTO updatePermission(String id, PermissionDTO request) {
    Permission permission = permissionRepository.findById(id).orElseThrow(
        () -> new BusinessException("Permission with name " + request.getName() + " not found"));
    permissionMapper.updatePermission(request, permission);
    return permissionMapper.toDTO(permission);
  }

  @Transactional
  public void deletePermission(String id) {
    Permission permission = permissionRepository.findById(id)
        .orElseThrow(() -> new BusinessException("Permission with id " + id + " not found"));
    permissionRepository.delete(permission);
  }

  public PermissionDTO getPermission(String id) {
    return permissionMapper.toDTO(permissionRepository.findById(id)
        .orElseThrow(() -> new BusinessException("Permission with id " + id + " not found")));
  }

  public List<PermissionDTO> getPermissions() {
    return permissionMapper.toDTO(permissionRepository.findAll());
  }
}
