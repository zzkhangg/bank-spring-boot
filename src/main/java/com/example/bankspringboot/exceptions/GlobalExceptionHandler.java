package com.example.bankspringboot.exceptions;

import com.example.bankspringboot.domain.response.RestResponse;
import java.nio.file.AccessDeniedException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleInvalidArgument(MethodArgumentNotValidException ex) {
    String enumKey = ex.getBindingResult().getFieldError().getDefaultMessage();
    ErrorCode errorCode = ErrorCode.INVALID_KEY;

    try {
      ErrorCode.valueOf(enumKey);
    } catch (IllegalArgumentException e) {

    }

    RestResponse<?> response = RestResponse.error(errorCode.getMessage(), errorCode.getCode());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleJsonParse(HttpMessageNotReadableException ex) {
    RestResponse<?> response =
        RestResponse.error(
            ErrorCode.JSON_PARSE_ERROR.getMessage(), ErrorCode.JSON_PARSE_ERROR.getCode());
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(BindException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleBindException(BindException ex) {
    RestResponse<?> res =
        RestResponse.error(
            ex.getBindingResult().getFieldError().getDefaultMessage(),
            HttpStatus.BAD_REQUEST.value());

    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(AppException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleException(AppException e) {
    RestResponse<?> res = RestResponse.error(e.getMessage(), e.getErrorCode().getCode());

    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    RestResponse<?> res =
        RestResponse.error(ErrorCode.TYPE_MISMATCH.getMessage(), ErrorCode.TYPE_MISMATCH.getCode());
    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(MissingPathVariableException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleMissingPathVariable(MissingPathVariableException e) {
    RestResponse<?> res =
        RestResponse.error(
            ErrorCode.MISSING_PATH_VARIABLE.getMessage(),
            ErrorCode.MISSING_PATH_VARIABLE.getCode());
    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {

    if (ex.getMessage().contains("email")) {
      RestResponse<?> res =
          RestResponse.error(
              ErrorCode.DATABASE_ERROR.getMessage(), ErrorCode.DATABASE_ERROR.getCode());
      return ResponseEntity.badRequest().body(res);
    }

    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleAccessDenied(AccessDeniedException ex) {
    RestResponse<?> res =
        RestResponse.error(ErrorCode.UNAUTHORIZED.getMessage(), ErrorCode.UNAUTHORIZED.getCode());

    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleBadCredentials(BadCredentialsException ex) {
    RestResponse<?> res =
        RestResponse.error(
            ErrorCode.UNAUTHENTICATED.getMessage(), ErrorCode.UNAUTHENTICATED.getCode());

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleException(RuntimeException e) {
    RestResponse<?> res =
        RestResponse.error(
            ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage(),
            ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());

    return ResponseEntity.badRequest().body(res);
  }
}
