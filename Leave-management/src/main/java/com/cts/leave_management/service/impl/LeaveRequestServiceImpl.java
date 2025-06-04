package com.cts.leave_management.service.impl;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;
import com.cts.leave_management.entity.Employee;
import com.cts.leave_management.entity.LeaveRequest;
import com.cts.leave_management.entity.enums.LeaveRequestStatus;
import com.cts.leave_management.entity.enums.LeaveType;
import com.cts.leave_management.repository.EmployeeRepository;
import com.cts.leave_management.repository.LeaveRequestRepository;
import com.cts.leave_management.service.LeaveRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public LeaveRequestDto createLeaveRequest(LeaveRequestDto leaveRequestDto) {
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setLeaveType(LeaveType.valueOf(leaveRequestDto.getLeaveType()));
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        leaveRequest.setReason("Requested Leave");

        // Fetch and set the employee
        Optional<Employee> employeeOpt = employeeRepository.findById(leaveRequestDto.getEmployeeId());
        if (employeeOpt.isPresent()) {
            leaveRequest.setEmployee(employeeOpt.get());
        } else {
            throw new RuntimeException("Employee not found with ID: " + leaveRequestDto.getEmployeeId());
        }

        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);

        return new LeaveRequestDto(
                savedLeaveRequest.getId(),
                savedLeaveRequest.getLeaveType().name(),
                savedLeaveRequest.getStartDate(),
                savedLeaveRequest.getEndDate(),
                savedLeaveRequest.getStatus().name(),
                savedLeaveRequest.getEmployee().getId()
        );
    }

    @Override
    public LeaveRequestDto updateLeaveRequestStatus(Long leaveId, String status) {
        Optional<LeaveRequest> leaveRequestOpt = leaveRequestRepository.findById(leaveId);
        if (leaveRequestOpt.isPresent()) {
            LeaveRequest leaveRequest = leaveRequestOpt.get();
            leaveRequest.setStatus(LeaveRequestStatus.valueOf(status));
            LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);

            return new LeaveRequestDto(
                    updatedLeaveRequest.getId(),
                    updatedLeaveRequest.getLeaveType().name(),
                    updatedLeaveRequest.getStartDate(),
                    updatedLeaveRequest.getEndDate(),
                    updatedLeaveRequest.getStatus().name(),
                    updatedLeaveRequest.getEmployee().getId()
            );
        }
        return null;
    }

    @Override
    public LeaveRequestDto getLeaveRequestById(Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId).orElse(null);
        if (leaveRequest == null) return null;

        return new LeaveRequestDto(
                leaveRequest.getId(),
                leaveRequest.getLeaveType().name(),
                leaveRequest.getStartDate(),
                leaveRequest.getEndDate(),
                leaveRequest.getStatus().name(),
                leaveRequest.getEmployee().getId()
        );
    }

    @Override
    public List<LeaveRequestResponseDto> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(req -> new LeaveRequestResponseDto(
                        req.getId(),
                        req.getLeaveType().name(),
                        req.getStartDate(),
                        req.getEndDate(),
                        req.getStatus().name(),
                        req.getReason(),
                        (req.getEmployee() != null) ? req.getEmployee().getEmployeeName() : "Unknown",
                        (req.getEmployee() != null) ? req.getEmployee().getId() : null
                ))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLeaveRequest(Long leaveId) {
        leaveRequestRepository.deleteById(leaveId);
    }
}
