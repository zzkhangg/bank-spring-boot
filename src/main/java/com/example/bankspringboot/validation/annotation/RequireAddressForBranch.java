package com.example.bankspringboot.validation.annotation;

import com.example.bankspringboot.validation.validator.RequireAddressForBranchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RequireAddressForBranchValidator.class)
public @interface RequireAddressForBranch {
    String message() default "Address is required for branch transactions.";
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
