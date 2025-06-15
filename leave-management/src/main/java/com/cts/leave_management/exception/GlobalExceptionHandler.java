package com.cts.leave_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
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
    public ResponseEntity<ErrorResponseEntity> InvalidRole(
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

    @ExceptionHandler(value=LeaveBalanceException.class)
    public ResponseEntity<ErrorResponseEntity> handleLeaveBalanceException(
            LeaveBalanceException ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.BAD_REQUEST;
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

    @ExceptionHandler(value=InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponseEntity> handleInvalidDateRangeException(
            InvalidDateRangeException ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.BAD_REQUEST;
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

    @ExceptionHandler(value=IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseEntity> handleIllegalArgumentException(
            IllegalArgumentException ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.BAD_REQUEST;
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

    @ExceptionHandler(value=DateTimeParseException.class)
    public ResponseEntity<ErrorResponseEntity> handleDateTimeParseException(
            DateTimeParseException ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponseEntity errorResponse = new ErrorResponseEntity(
                LocalTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "Invalid date format. Please use 'yyyy-MM-dd'. " + ex.getMessage(),
                request.getDescription(false).replace("uri=",""),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        Map<String, List<String>> errorResponse = new HashMap<>();
        errorResponse.put("errors", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseEntity> handleGeneralException(
            Exception ex,
            WebRequest request
    ){
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorResponseEntity errorResponse = new ErrorResponseEntity(
                LocalTime.now(),
                status.value(),
                status.getReasonPhrase(),
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false).replace("uri=",""),
                ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(errorResponse, status);
    }
}