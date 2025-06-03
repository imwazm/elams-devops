package com.cts.attendance_management.service;

import com.cts.attendance_management.dto.LeaveBalanceRequestDto;
import com.cts.attendance_management.dto.LeaveBalanceResponseDto;
import com.cts.attendance_management.entity.enums.LeaveType;

import java.util.List;

public interface LeaveBalanceService {

    LeaveBalanceResponseDto addLeaveBalance(LeaveBalanceRequestDto leaveBalanceDto);

    List<LeaveBalanceResponseDto> findAllLeaveBalances();

    LeaveBalanceResponseDto findLeaveBalanceById(Long id);


    List<LeaveBalanceResponseDto> findLeaveBalancesByEmployeeId(Long employeeId);


    LeaveBalanceResponseDto updateLeaveBalance(Long id, LeaveBalanceRequestDto leaveBalanceDto);


    LeaveBalanceResponseDto adjustLeaveBalance(Long employeeId, LeaveType leaveType, int days, boolean isApproved);


    void deleteLeaveBalance(Long id);


    void initializeLeaveBalancesForNewEmployee(Long employeeId);
}