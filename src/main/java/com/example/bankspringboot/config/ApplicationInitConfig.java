package com.example.bankspringboot.config;

import com.example.bankspringboot.common.PredefinedPermission;
import com.example.bankspringboot.common.Role;
import com.example.bankspringboot.domain.Permission;
import com.example.bankspringboot.domain.admin.Admin;
import com.example.bankspringboot.repository.AdminRespository;
import com.example.bankspringboot.repository.PermissionRepository;
import com.example.bankspringboot.repository.RoleRepository;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

  @NonFinal
  static final String ADMIN_USER_NAME = "admin";

  @NonFinal
  static final String ADMIN_PASSWORD = "admin";

  @Transactional
  @Bean
  @Profile("!test")
  ApplicationRunner applicationRunner(
      AdminRespository adminRespository, PasswordEncoder passwordEncoder,
      PermissionRepository permissionRepository, RoleRepository roleRepository) {
    return args -> {
      for (PredefinedPermission permission : PredefinedPermission.values()) {
        if (!permissionRepository.existsById(permission.toString())) {
          permissionRepository.save(
              Permission.builder()
                  .name(permission.toString())
                  .description(permission.getDescription())
                  .build());
        }
      }

      for (Role role : Role.values()) {
        if (!roleRepository.existsById(role.toString())) {
          Set<Permission> permissions = new HashSet<>();
          for (PredefinedPermission permission : role.getPermissions()) {
            Permission p = permissionRepository.findById(permission.toString())
                .orElseGet(() -> permissionRepository.save(
                    Permission.builder()
                        .name(permission.toString())
                        .description(permission.getDescription())
                        .build()
                ));

            permissions.add(p);
          }
          roleRepository.save(com.example.bankspringboot.domain.Role.builder()
              .name(role.toString())
              .permissions(permissions)
              .description(role.getDescription())
              .build());
        }
      }

      if (adminRespository.findByEmail(ApplicationInitConfig.ADMIN_USER_NAME).isEmpty()) {
        com.example.bankspringboot.domain.Role adminRole = roleRepository.findById(Role.ADMIN.name())
            .orElseThrow();

        adminRespository.save(
            Admin.builder()
                .email(ApplicationInitConfig.ADMIN_USER_NAME)
                .password(passwordEncoder.encode(ApplicationInitConfig.ADMIN_PASSWORD))
                .roles(Set.of(adminRole))
                .build());
        log.warn("admin user has been created with default password: admin, please change it");
      }
      log.info("Application initialization completed .....");
    };
  }
}
