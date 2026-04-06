package com.cts.attendance_management.exception;

import feign.FeignException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class FeignExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignExceptions(FeignException ex){
        return ResponseEntity.status(ex.status())
                .contentType(MediaType.APPLICATION_JSON)
                .body(ex.contentUTF8());
    }
}
