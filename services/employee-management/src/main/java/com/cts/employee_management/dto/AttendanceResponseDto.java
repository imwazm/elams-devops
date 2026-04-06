package com.cts.employee_management.dto;

import com.cts.employee_management.entity.enums.AttendanceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponseDto {
    private Long id;
    private LocalTime clockInTime;
    private LocalTime clockOutTime;
    private double workHours;
    private LocalDate date;
    private AttendanceStatus status;
    private Long employeeId;
}