package com.example.bankspringboot.domain.admin;

import com.example.bankspringboot.domain.User;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
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
@PrimaryKeyJoinColumn(name = "user_id")
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends User {}
