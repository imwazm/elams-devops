package com.cts.attendance_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceClockOutRequestDto {
    private LocalTime clockOutTime;
    private LocalDate date;
    private Long employeeId;
}