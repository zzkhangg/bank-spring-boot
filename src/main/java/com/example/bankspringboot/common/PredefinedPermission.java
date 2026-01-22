package com.example.bankspringboot.common;

import lombok.Getter;

@Getter
public enum PredefinedPermission {
  SEE_PERSONAL_INFO("See personal information"),
  SEE_TRANSACTION_HISTORY("See transaction history"),
  SEE_ALL_TRANSACTIONS("See all transactions"),
  SEE_ALL_CUSTOMER_INFO("See all customer information"),
  CREATE_ACCOUNT("Create account"),
  DELETE_ACCOUNT("Delete account");

  private final String description;

  PredefinedPermission(String description) {
    this.description = description;
  }
}
