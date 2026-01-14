package com.example.bankspringboot.service;

import java.util.function.Supplier;
import org.springframework.stereotype.Service;

@Service
public class ActionExecutor {

  public <T> T execute(Supplier<T> action) {
    return action.get();
  }
}
