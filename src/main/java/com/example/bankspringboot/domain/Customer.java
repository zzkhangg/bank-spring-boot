package com.example.bankspringboot.domain;

import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;

import java.time.LocalDate;


@Entity
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fname;
    private String lname;
    private String email;
    private String phone;
    private LocalDate birthdate;
    private String password;

    @OneToMany(mappedBy = "customer")
    private List<Account> accounts;

    @OneToMany(mappedBy = "customer")
    private List<Transaction> transactions;

    public Customer() {}

    public Customer(String fname, String lname, String email, String phone, LocalDate birthdate) {
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.phone = phone;
        this.birthdate = birthdate;
    }

    /* Getters */
    public Long getId() {
        return this.id;
    }

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

    public String getPassword() {
        return this.password;
    }

    /* Setters */
    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;
        if (!(o instanceof Customer))
            return false;
        Customer customer = (Customer) o;
        return Objects.equals(this.id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "Customer{" + "id=" + this.id + ", name='" + this.lname + this.fname + '\'' + '}';
    }
}