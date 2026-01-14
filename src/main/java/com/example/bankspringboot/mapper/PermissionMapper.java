package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.Permission;
import com.example.bankspringboot.dto.permission.PermissionDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
  Permission toPermission(PermissionDTO dto);

  PermissionDTO toDTO(Permission entity);

  List<PermissionDTO> toDTO(List<Permission> entity);

  void updatePermission(PermissionDTO dto, @MappingTarget Permission entity);
}
