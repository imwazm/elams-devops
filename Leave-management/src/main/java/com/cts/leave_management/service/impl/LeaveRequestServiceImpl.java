package com.cts.leave_management.service.impl;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;
import com.cts.leave_management.entity.Employee;
import com.cts.leave_management.entity.LeaveRequest;
import com.cts.leave_management.entity.enums.LeaveRequestStatus;
import com.cts.leave_management.entity.enums.LeaveType;
import com.cts.leave_management.exception.ResourceNotFoundException;
import com.cts.leave_management.repository.EmployeeRepository;
import com.cts.leave_management.repository.LeaveRequestRepository;
import com.cts.leave_management.service.LeaveRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public LeaveRequestDto createLeaveRequest(LeaveRequestDto leaveRequestDto) {
        LeaveRequest leaveRequest = modelMapper.map(leaveRequestDto, LeaveRequest.class);
        leaveRequest.setLeaveType(LeaveType.valueOf(leaveRequestDto.getLeaveType()));
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);

        Employee employee = employeeRepository.findById(leaveRequestDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + leaveRequestDto.getEmployeeId()));
        leaveRequest.setEmployee(employee);

        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return convertToDto(savedLeaveRequest);
    }

    @Override
    public LeaveRequestDto updateLeaveRequestStatus(Long leaveId, LeaveRequestStatus status) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        leaveRequest.setStatus(status);
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);

        return convertToDto(updatedLeaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDto> getAllLeaveRequests() {
        return leaveRequestRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveRequestDto getLeaveRequestById(Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        return convertToDto(leaveRequest);
    }

    @Override
    public List<LeaveRequestResponseDto> getLeaveRequestsByStatus(LeaveRequestStatus status) {
        return leaveRequestRepository.findByStatus(status).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDto> getLeaveRequestsByStatusAndEmployee(LeaveRequestStatus status, Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        return leaveRequestRepository.findByStatusAndEmployee(status, employee).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDto> getLeaveRequestsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));

        return leaveRequestRepository.findByEmployee(employee).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteLeaveRequest(Long leaveId) {
        if (!leaveRequestRepository.existsById(leaveId)) {
            throw new ResourceNotFoundException("Leave request not found with ID: " + leaveId);
        }
        leaveRequestRepository.deleteById(leaveId);
    }

    private LeaveRequestDto convertToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto leaveRequestDto = modelMapper.map(leaveRequest, LeaveRequestDto.class);
        leaveRequestDto.setEmployeeId(leaveRequest.getEmployee().getId());
        return leaveRequestDto;
    }

    private LeaveRequestResponseDto convertToResponseDto(LeaveRequest leaveRequest) {
        LeaveRequestResponseDto responseDto = modelMapper.map(leaveRequest, LeaveRequestResponseDto.class);
        responseDto.setEmployeeId(leaveRequest.getEmployee().getId());
        responseDto.setEmployeeName(leaveRequest.getEmployee().getEmployeeName());
        return responseDto;
    }
}