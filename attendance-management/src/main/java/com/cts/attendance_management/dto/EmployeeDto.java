package com.cts.attendance_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private Long id;
    String employeeName;
    String email;
    private Long shiftId;
    private Long managerId;
}