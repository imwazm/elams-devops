package com.cts.leave_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDto {
    private Long leaveId;
    private String leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private Long employeeId;
}