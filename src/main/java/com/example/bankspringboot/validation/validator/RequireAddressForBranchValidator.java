package com.example.bankspringboot.validation.validator;

import com.example.bankspringboot.domain.transaction.TransactionChannel;
import com.example.bankspringboot.dto.common.BranchTransaction;
import com.example.bankspringboot.validation.annotation.RequireAddressForBranch;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RequireAddressForBranchValidator implements ConstraintValidator<RequireAddressForBranch, BranchTransaction> {

    @Override
    public boolean isValid(BranchTransaction request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        if (request.getChannel() != TransactionChannel.BRANCH) {
            return true;
        }

        if (request.getAddress() != null) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        context.buildConstraintViolationWithTemplate(
                        "Address is required for branch transactions.")
                .addPropertyNode("address")
                .addConstraintViolation();

        return false;
    }
}
