package com.cts.leave_management.service;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;

import java.util.List;

public interface LeaveRequestService {
    LeaveRequestDto createLeaveRequest(LeaveRequestDto leaveRequestDto);
    LeaveRequestDto updateLeaveRequestStatus(Long leaveId, String status);
    List<LeaveRequestResponseDto> getAllLeaveRequests();
    LeaveRequestDto getLeaveRequestById(Long leaveId);
    void deleteLeaveRequest(Long leaveId);
}
