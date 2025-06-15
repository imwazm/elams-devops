package com.cts.employee_management.dto;

import com.cts.employee_management.entity.Employee;
import com.cts.employee_management.entity.enums.AttendanceReportType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReportDto {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalPresent;
    private int totalAbsent;
    private AttendanceReportType type;
    private Employee employee;
}