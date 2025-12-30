package com.example.bankspringboot.dto.account;

import com.example.bankspringboot.domain.account.AccountStatus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAccountRequest {

  AccountStatus status; // ACTIVE, FROZEN, CLOSED

  String reason;
}
