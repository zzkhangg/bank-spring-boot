package com.example.bankspringboot.exceptions;

import com.example.bankspringboot.domain.response.ErrorResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = IdInvalidException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleIdInvalidException(IdInvalidException e) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Failure");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setErrors(e.getMessage());

    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
    BindingResult bindingResult = ex.getBindingResult();
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Failure");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    List<String> errors = bindingResult.getFieldErrors()
        .stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .toList();
    res.setErrors(errors);
    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleJsonParse(HttpMessageNotReadableException ex) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Invalid JSON Format");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setErrors(ex.getMessage());

    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(BindException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Invalid Request");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    BindingResult bindingResult = ex.getBindingResult();
    List<String> errors = bindingResult.getFieldErrors()
        .stream()
        .map(e -> e.getField() + ": " + e.getDefaultMessage())
        .toList();
    res.setErrors(errors);

    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(BusinessException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleException(BusinessException e) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Bad Request");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setErrors(e.getMessage());

    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Bad Request");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setErrors(e.getMessage());
    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(MissingServletRequestParameterException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Bad Request");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setErrors(e.getMessage());
    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(MissingPathVariableException.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleMissingPathVariable(MissingPathVariableException e) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Bad Request");
    res.setStatusCode(HttpStatus.BAD_REQUEST.value());
    res.setErrors(e.getMessage());
    return ResponseEntity.badRequest().body(res);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {

    if (ex.getMessage().contains("email")) {
      ErrorResponse res = ErrorResponse.builder()
          .message("Email already exists")
          .statusCode(HttpStatus.CONFLICT.value())
          .errors(ex.getMessage())
          .build();
      return ResponseEntity.badRequest().body(res);
    }

    return ResponseEntity.internalServerError().build();
  }

  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    ErrorResponse res = new ErrorResponse();
    res.setMessage("Unknown Error");
    res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    res.setErrors("Internal Server Error");

    return ResponseEntity.badRequest().body(res);
  }
}
