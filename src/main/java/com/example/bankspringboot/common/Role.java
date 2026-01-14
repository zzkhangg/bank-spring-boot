package com.example.bankspringboot.common;

import static com.example.bankspringboot.common.PredefinedPermission.SEE_PERSONAL_INFO;
import static com.example.bankspringboot.common.PredefinedPermission.SEE_TRANSACTION_HISTORY;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

@Getter
public enum Role {
  CUSTOMER("Customer role", SEE_PERSONAL_INFO, SEE_TRANSACTION_HISTORY),
  ADMIN("Admin role", PredefinedPermission.values());

  private final Set<PredefinedPermission> permissions;
  private final String description;

  Role( String description, PredefinedPermission... permissions) {
    this.description = description;
    this.permissions = new HashSet<>(Arrays.asList(permissions));
  }
}
