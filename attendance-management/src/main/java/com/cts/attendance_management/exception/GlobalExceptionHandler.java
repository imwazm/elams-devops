package com.cts.attendance_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalTime;

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
