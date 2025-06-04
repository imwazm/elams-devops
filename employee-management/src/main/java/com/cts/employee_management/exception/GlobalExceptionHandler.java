package com.cts.employee_management.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler(value=ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseEntity> ResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponseEntity errorResponse = new ErrorResponseEntity(
                LocalTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=",""),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(value=InvalidRoleException.class)
    public ResponseEntity<ErrorResponseEntity> ResourceNotFound(
            InvalidRoleException ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ErrorResponseEntity errorResponse = new ErrorResponseEntity(
                LocalTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=",""),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();

         String errorMessage = ex.getConstraintViolations().stream()
                 .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                 .collect(Collectors.joining(", "));
         errors.put("errors", errorMessage);


        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseEntity> exceptionHandler(
            Exception ex, WebRequest request
    ){
        HttpStatus status = HttpStatus.NOT_FOUND;
        ErrorResponseEntity errorResponse = new ErrorResponseEntity(
                LocalTime.now(),
                status.value(),
                status.getReasonPhrase(),
                ex.getMessage(),
                request.getDescription(false).replace("uri=",""),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

}
