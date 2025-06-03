package com.cts.employee_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDto {
    private Long ShiftID;
    private LocalDate ShiftDate;
    private LocalTime ShiftTime;
    private Long employeeId;
}
