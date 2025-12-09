package com.example.bankspringboot.domain.response;

public class ErrorResponse {

  private String message;
  private int statusCode;
  private Object errors;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public Object getError() {
    return errors;
  }

  public void setError(Object errors) {
    this.errors = errors;
  }
}
