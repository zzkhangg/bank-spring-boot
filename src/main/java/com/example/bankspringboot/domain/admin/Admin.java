package com.example.bankspringboot.domain.admin;

import com.example.bankspringboot.domain.User;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import lombok.AccessLevel;
import lombok.Getter;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@DiscriminatorValue("ADMIN")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends User {
}