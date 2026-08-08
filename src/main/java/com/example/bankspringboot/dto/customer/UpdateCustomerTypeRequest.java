package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.common.CustomerTypeCode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCustomerTypeRequest {
    @NotNull
    CustomerTypeCode customerType;
}
