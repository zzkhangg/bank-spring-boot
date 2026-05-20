package com.example.bankspringboot.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public enum ErrorCode {
  INVALID_KEY("Invalid error code key", 1001, HttpStatus.INTERNAL_SERVER_ERROR),

  RESOURCE_NOT_FOUND("Resource not found", 1100, HttpStatus.NOT_FOUND),
  RESOURCE_EXISTED("Resource already exists", 1101, HttpStatus.CONFLICT),

  VALIDATION_ERROR("Validation failed", 1200, HttpStatus.BAD_REQUEST),
  JSON_PARSE_ERROR("Malformed JSON request", 1201, HttpStatus.BAD_REQUEST),
  TYPE_MISMATCH("Type mismatch", 1202, HttpStatus.BAD_REQUEST),

  UNAUTHORIZED("You do not have permission to access this resource", 1300, HttpStatus.FORBIDDEN),
  UNAUTHENTICATED("Authentication is required", 1301, HttpStatus.UNAUTHORIZED),

  DATABASE_ERROR("Database error", 1400, HttpStatus.INTERNAL_SERVER_ERROR),

  MISSING_PATH_VARIABLE("Missing path variable", 1500, HttpStatus.BAD_REQUEST),

  TRANSFER_ERROR("Transfer could not be completed", 1600, HttpStatus.BAD_REQUEST),
  UPDATE_ERROR("Update could not be completed", 1601, HttpStatus.BAD_REQUEST),
  CREATE_ERROR("Create operation could not be completed", 1602, HttpStatus.BAD_REQUEST),
  REQUEST_IN_PROGRESS("Request is still processing", 1603, HttpStatus.CONFLICT),
  IDEMPOTENCY_KEY_REUSED_WITH_DIFFERENT_REQUEST(
      "Idempotency key was reused with a different request", 1604, HttpStatus.CONFLICT),
  REQUEST_FAILED("Request failed", 1605, HttpStatus.INTERNAL_SERVER_ERROR),

  UNCATEGORIZED_EXCEPTION("Unexpected server error", 9999, HttpStatus.INTERNAL_SERVER_ERROR),
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
