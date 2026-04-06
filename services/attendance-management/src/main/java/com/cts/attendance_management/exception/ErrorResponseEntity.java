package com.cts.attendance_management.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseEntity {
    private LocalTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String exception;
}
