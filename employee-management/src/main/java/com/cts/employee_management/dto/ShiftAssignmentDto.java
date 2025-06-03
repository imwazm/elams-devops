package com.cts.employee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftAssignmentDto {
    private Long employeeId;
    private Long shiftId;
}