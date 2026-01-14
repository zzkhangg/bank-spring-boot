package com.example.bankspringboot.mapper;

import com.example.bankspringboot.domain.Role;
import com.example.bankspringboot.dto.role.RoleRequest;
import com.example.bankspringboot.dto.role.RoleResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {
  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toResponse(Role role);
}
