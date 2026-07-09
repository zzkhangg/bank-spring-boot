package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.Permission;
import com.example.bankspringboot.dto.permission.PermissionDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
  @Mapping(target = "roles", ignore = true)
  Permission toPermission(PermissionDTO dto);

  PermissionDTO toDTO(Permission entity);

  List<PermissionDTO> toDTO(List<Permission> entity);

  @Mapping(target = "roles", ignore = true)
  void updatePermission(PermissionDTO dto, @MappingTarget Permission entity);
}
