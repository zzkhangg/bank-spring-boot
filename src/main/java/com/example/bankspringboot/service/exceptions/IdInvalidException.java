package com.example.bankspringboot.service.exceptions;

public class IdInvalidException extends RuntimeException {

  private String message;

  public IdInvalidException(String message) {
    super(message);
    this.message = message;
  }
}
