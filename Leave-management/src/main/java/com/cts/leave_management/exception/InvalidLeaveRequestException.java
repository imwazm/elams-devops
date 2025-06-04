package com.cts.leave_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST) // Or HttpStatus.CONFLICT, depending on typical usage
public class InvalidLeaveRequestException extends RuntimeException {
    public InvalidLeaveRequestException(String message) {
        super(message);
    }

    public InvalidLeaveRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}