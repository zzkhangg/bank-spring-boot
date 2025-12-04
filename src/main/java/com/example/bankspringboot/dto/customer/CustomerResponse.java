package com.example.bankspringboot.dto.customer;

import com.example.bankspringboot.domain.common.Address;
import lombok.Getter;

@Getter
public class CustomerResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Address address;
    Long id;

    public CustomerResponse(Long id, String firstName, String lastName, String email, String phone, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.id = id;
        this.address = address;
    }
}
