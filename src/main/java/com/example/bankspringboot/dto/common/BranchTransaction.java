package com.example.bankspringboot.dto.common;

import com.example.bankspringboot.domain.common.Address;
import com.example.bankspringboot.domain.transaction.TransactionChannel;

public interface BranchTransaction {

    TransactionChannel getChannel();

    Address getAddress();
}
