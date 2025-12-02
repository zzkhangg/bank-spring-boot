package com.example.bankspringboot.dto.customer;

public class CustomerResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    Long id;

    public CustomerResponse(Long id, String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }


    public String getPhone() {
        return phone;
    }


    public Long getId() {
        return id;
    }
}
