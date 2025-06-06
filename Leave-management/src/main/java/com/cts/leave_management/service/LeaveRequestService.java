package com.cts.leave_management.service;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;
import com.cts.leave_management.entity.enums.LeaveRequestStatus;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequestDto createLeaveRequest(LeaveRequestDto leaveRequestDto);
    LeaveRequestDto updateLeaveRequestStatus(Long leaveId, LeaveRequestStatus status);
    List<LeaveRequestResponseDto> getAllLeaveRequests();
    LeaveRequestDto getLeaveRequestById(Long leaveId);
    List<LeaveRequestResponseDto> getLeaveRequestsByStatus(LeaveRequestStatus status);
    List<LeaveRequestResponseDto> getLeaveRequestsByStatusAndEmployee(LeaveRequestStatus status, Long employeeId);
    List<LeaveRequestResponseDto> getLeaveRequestsByEmployee(Long employeeId);
    void deleteLeaveRequest(Long leaveId);
}