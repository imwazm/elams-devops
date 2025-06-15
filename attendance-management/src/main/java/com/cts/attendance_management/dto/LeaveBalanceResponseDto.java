package com.cts.attendance_management.dto;

import com.cts.attendance_management.entity.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalanceResponseDto {
    private Long id;
    private LeaveType leaveType;
    private int balance;
    private Long employeeId;
    private String employeeName;
}