package com.cts.leave_management.controller;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;
import com.cts.leave_management.entity.enums.LeaveRequestStatus;
import com.cts.leave_management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("api/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveRequestDto> createLeaveRequest(@RequestBody LeaveRequestDto leaveRequestDto) {
        LeaveRequestDto createdLeaveRequest = leaveRequestService.createLeaveRequest(leaveRequestDto);
        return ResponseEntity.status(201).body(createdLeaveRequest);
    }

    @PutMapping("{leaveId}/status")
    public ResponseEntity<LeaveRequestDto> updateLeaveRequestStatus(@PathVariable Long leaveId, @RequestParam LeaveRequestStatus status) {
        LeaveRequestDto updatedLeaveRequest = leaveRequestService.updateLeaveRequestStatus(leaveId, status);
        return ResponseEntity.ok(updatedLeaveRequest);
    }

    @GetMapping
    public ResponseEntity<List<LeaveRequestResponseDto>> getAllLeaveRequests() {
        List<LeaveRequestResponseDto> leaveRequests = leaveRequestService.getAllLeaveRequests();
        return ResponseEntity.ok(leaveRequests);
    }

    @GetMapping("{leaveId}")
    public ResponseEntity<LeaveRequestDto> getLeaveRequestById(@PathVariable Long leaveId) {
        LeaveRequestDto leaveRequest = leaveRequestService.getLeaveRequestById(leaveId);
        return ResponseEntity.ok(leaveRequest);
    }

    @GetMapping("by-status")
    public ResponseEntity<List<LeaveRequestResponseDto>> getLeaveRequestsByStatus(@RequestParam LeaveRequestStatus status) {
        List<LeaveRequestResponseDto> leaveRequests = leaveRequestService.getLeaveRequestsByStatus(status);
        return ResponseEntity.ok(leaveRequests);
    }

    @GetMapping("by-status-and-employee")
    public ResponseEntity<List<LeaveRequestResponseDto>> getLeaveRequestsByStatusAndEmployee(
            @RequestParam LeaveRequestStatus status, @RequestParam Long employeeId) {
        List<LeaveRequestResponseDto> leaveRequests = leaveRequestService.getLeaveRequestsByStatusAndEmployee(status, employeeId);
        return ResponseEntity.ok(leaveRequests);
    }

    @GetMapping("by-employee")
    public ResponseEntity<List<LeaveRequestResponseDto>> getLeaveRequestsByEmployee(@RequestParam Long employeeId) {
        List<LeaveRequestResponseDto> leaveRequests = leaveRequestService.getLeaveRequestsByEmployee(employeeId);
        return ResponseEntity.ok(leaveRequests);
    }

    @DeleteMapping("{leaveId}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long leaveId) {
        leaveRequestService.deleteLeaveRequest(leaveId);
        return ResponseEntity.noContent().build();
    }
}