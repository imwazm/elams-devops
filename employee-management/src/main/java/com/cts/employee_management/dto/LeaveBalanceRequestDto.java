package com.cts.employee_management.dto;

import com.cts.employee_management.entity.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceRequestDto {
    private Long id;
    private LeaveType leaveType;
    private int balance;
    private Long employeeId;
}