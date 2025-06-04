package com.cts.leave_management.controller;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;
import com.cts.leave_management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leave-requests")
public class LeaveRequestController {

    @Autowired
    private LeaveRequestService leaveRequestService;

    @PostMapping
    public ResponseEntity<LeaveRequestDto> createLeaveRequest(@RequestBody LeaveRequestDto leaveRequestDto) {
        return ResponseEntity.ok(leaveRequestService.createLeaveRequest(leaveRequestDto));
    }

    @PutMapping("/{leaveId}/status")
    public ResponseEntity<LeaveRequestDto> updateLeaveRequestStatus(@PathVariable Long leaveId, @RequestParam String status) {
        return ResponseEntity.ok(leaveRequestService.updateLeaveRequestStatus(leaveId, status));
    }

    @GetMapping("/getRequests")
    public ResponseEntity<List<LeaveRequestResponseDto>> getAllLeaveRequests() {
        return ResponseEntity.ok(leaveRequestService.getAllLeaveRequests());
    }

    @GetMapping("/{leaveId}")
    public ResponseEntity<LeaveRequestDto> getLeaveRequestById(@PathVariable Long leaveId) {
        return ResponseEntity.ok(leaveRequestService.getLeaveRequestById(leaveId));
    }

    @DeleteMapping("/{leaveId}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long leaveId) {
        leaveRequestService.deleteLeaveRequest(leaveId);
        return ResponseEntity.noContent().build();
    }
}
