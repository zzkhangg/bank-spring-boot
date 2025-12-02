package com.example.bankspringboot.service.exceptions;

import com.example.bankspringboot.domain.response.ErrorResponse;
import com.example.bankspringboot.domain.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = IdInvalidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleIdInvalidException(IdInvalidException e) {
        ErrorResponse res = new ErrorResponse();
        res.setMessage("Failure");
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(e.getMessage());

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleInvalidArgument(MethodArgumentNotValidException e) {
        ErrorResponse res = new ErrorResponse();
        res.setMessage("Failure");
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        List<String> errors = List.of(e.getMessage());
        res.setError(errors);

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleJsonParse(HttpMessageNotReadableException ex) {
        ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid JSON Format");
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError(List.of(ex.getMessage()));

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid Request");
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        if (e.getBindingResult().hasErrors()) {
            res.setError(e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
        }

        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse res = new ErrorResponse();
        res.setMessage("Invalid Request");
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Internal Server Error");

        return ResponseEntity.badRequest().body(res);
    }
}
