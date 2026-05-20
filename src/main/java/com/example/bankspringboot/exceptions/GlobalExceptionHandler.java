package com.example.bankspringboot.exceptions;

import com.example.bankspringboot.domain.response.RestResponse;
import java.nio.file.AccessDeniedException;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
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
    String message =
        Optional.ofNullable(ex.getBindingResult().getFieldError())
            .map(error -> error.getDefaultMessage())
            .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

    RestResponse<?> response = RestResponse.error(message, ErrorCode.VALIDATION_ERROR.getCode());
    return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatusCode()).body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleJsonParse(HttpMessageNotReadableException ex) {
    RestResponse<?> response =
        RestResponse.error(
            ErrorCode.JSON_PARSE_ERROR.getMessage(), ErrorCode.JSON_PARSE_ERROR.getCode());
    return ResponseEntity.status(ErrorCode.JSON_PARSE_ERROR.getStatusCode()).body(response);
  }

  @ExceptionHandler(BindException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleBindException(BindException ex) {
    String message =
        Optional.ofNullable(ex.getBindingResult().getFieldError())
            .map(error -> error.getDefaultMessage())
            .orElse(ErrorCode.VALIDATION_ERROR.getMessage());

    RestResponse<?> res =
        RestResponse.error(message, ErrorCode.VALIDATION_ERROR.getCode());

    return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.getStatusCode()).body(res);
  }

  @ExceptionHandler(AppException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleException(AppException e) {
    RestResponse<?> res = RestResponse.error(e.getMessage(), e.getErrorCode().getCode());

    return ResponseEntity.status(e.getErrorCode().getStatusCode()).body(res);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException e) {
    RestResponse<?> res =
        RestResponse.error(ErrorCode.TYPE_MISMATCH.getMessage(), ErrorCode.TYPE_MISMATCH.getCode());
    return ResponseEntity.status(ErrorCode.TYPE_MISMATCH.getStatusCode()).body(res);
  }

  @ExceptionHandler(MissingPathVariableException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleMissingPathVariable(MissingPathVariableException e) {
    RestResponse<?> res =
        RestResponse.error(
            ErrorCode.MISSING_PATH_VARIABLE.getMessage(),
            ErrorCode.MISSING_PATH_VARIABLE.getCode());
    return ResponseEntity.status(ErrorCode.MISSING_PATH_VARIABLE.getStatusCode()).body(res);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleDataIntegrityViolation(
      DataIntegrityViolationException ex) {

    if (ex.getMessage().contains("email")) {
      RestResponse<?> res =
          RestResponse.error(
              ErrorCode.RESOURCE_EXISTED.getMessage(), ErrorCode.RESOURCE_EXISTED.getCode());
      return ResponseEntity.status(ErrorCode.RESOURCE_EXISTED.getStatusCode()).body(res);
    }

    RestResponse<?> res =
        RestResponse.error(ErrorCode.DATABASE_ERROR.getMessage(), ErrorCode.DATABASE_ERROR.getCode());
    return ResponseEntity.status(ErrorCode.DATABASE_ERROR.getStatusCode()).body(res);
  }

  @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleAccessDenied(AccessDeniedException ex) {
    RestResponse<?> res =
        RestResponse.error(ErrorCode.UNAUTHORIZED.getMessage(), ErrorCode.UNAUTHORIZED.getCode());

    return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getStatusCode()).body(res);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleBadCredentials(BadCredentialsException ex) {
    RestResponse<?> res =
        RestResponse.error(
            ErrorCode.UNAUTHENTICATED.getMessage(), ErrorCode.UNAUTHENTICATED.getCode());

    return ResponseEntity.status(ErrorCode.UNAUTHENTICATED.getStatusCode()).body(res);
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseBody
  public ResponseEntity<RestResponse<?>> handleException(RuntimeException e) {
    RestResponse<?> res =
        RestResponse.error(
            ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage(),
            ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());

    return ResponseEntity.status(ErrorCode.UNCATEGORIZED_EXCEPTION.getStatusCode()).body(res);
  }
}
