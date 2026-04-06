package com.cts.leave_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceClockInRequestDto {
    private LocalTime clockInTime;
    private LocalDate date;
    private Long employeeId;
}