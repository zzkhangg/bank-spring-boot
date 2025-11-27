package com.example.bankspringboot.dto.customer;

import java.time.LocalDate;

public class CreateCustomerRequest {
    private String fname;
    private String lname;
    private String email;
    private String phone;
    private LocalDate birthdate;

    public String getFname() {
        return this.fname;
    }
    public String getLname() {
        return this.lname;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPhone() {
        return this.phone;
    }
}