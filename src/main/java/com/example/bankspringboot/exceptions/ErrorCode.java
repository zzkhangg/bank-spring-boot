package com.example.bankspringboot.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum ErrorCode {
  INVALID_KEY("Invalid key for error code", 1001, HttpStatus.INTERNAL_SERVER_ERROR),

  RESOURCE_NOT_FOUND("Resource Found", 1100, HttpStatus.NOT_FOUND),
  RESOURCE_EXISTED("Resource Existed", 1101, HttpStatus.BAD_REQUEST),

  VALIDATION_ERROR("Validation Error", 1200, HttpStatus.BAD_REQUEST),
  JSON_PARSE_ERROR("JSON Parse Error", 1201, HttpStatus.BAD_REQUEST),
  TYPE_MISMATCH("Type mismatch", 1202, HttpStatus.BAD_REQUEST),

  UNAUTHORIZED("Unauthorized", 1300, HttpStatus.FORBIDDEN),
  UNAUTHENTICATED("Unauthenticated", 1301, HttpStatus.UNAUTHORIZED),

  DATABASE_ERROR("Database Error", 1400, HttpStatus.INTERNAL_SERVER_ERROR),

  MISSING_PATH_VARIABLE("Missing Path Variable", 1500, HttpStatus.BAD_REQUEST),

  TRANSFER_ERROR("Transfer Error", 1600, HttpStatus.BAD_REQUEST),
  UPDATE_ERROR("Update Error", 1601, HttpStatus.BAD_REQUEST),
  CREATE_ERROR("Create Error", 1602, HttpStatus.BAD_REQUEST),
  REQUEST_IN_PROGRESS("Request In Progress", 1603, HttpStatus.BAD_REQUEST),

  UNCATEGORIZED_EXCEPTION("Uncategorized Exception", 9999, HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  String message;
  int code;
  HttpStatus statusCode;

  ErrorCode(String message, int code, HttpStatus statusCode) {
    this.message = message;
    this.code = code;
    this.statusCode = statusCode;
  }
}
