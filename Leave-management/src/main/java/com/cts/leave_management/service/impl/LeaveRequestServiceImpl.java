package com.cts.leave_management.service.impl;

import com.cts.leave_management.dto.LeaveRequestDto;
import com.cts.leave_management.dto.LeaveRequestResponseDto;
import com.cts.leave_management.entity.LeaveRequest;
import com.cts.leave_management.entity.enums.LeaveRequestStatus;
import com.cts.leave_management.entity.enums.LeaveType;
import com.cts.leave_management.exception.ResourceNotFoundException;
import com.cts.leave_management.exception.LeaveBalanceException;
import com.cts.leave_management.exception.InvalidDateRangeException;
import com.cts.leave_management.repository.LeaveRequestRepository;
import com.cts.leave_management.service.LeaveBalanceService;
import com.cts.leave_management.service.LeaveRequestService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestServiceImpl implements LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    private int calculateWorkingDays(LocalDate startDate, LocalDate endDate) {
        if (endDate.isBefore(startDate)) {
            throw new InvalidDateRangeException("End date (" + endDate + ") cannot be before start date (" + startDate + ").");
        }

        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null for calculating working days.");
        }

        int workingDays = 0;
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY) {
                workingDays++;
            }
        }
        return workingDays;
    }

    @Override
    @Transactional
    public LeaveRequestDto createLeaveRequest(LeaveRequestDto leaveRequestDto) {

        LeaveRequest leaveRequest = modelMapper.map(leaveRequestDto, LeaveRequest.class);
        leaveRequest.setLeaveType(LeaveType.valueOf(leaveRequestDto.getLeaveType()));
        leaveRequest.setStatus(LeaveRequestStatus.PENDING);
        leaveRequest.setEmployeeId(leaveRequestDto.getEmployeeId());

        int calculatedDays = calculateWorkingDays(leaveRequest.getStartDate(), leaveRequest.getEndDate());

        try {
            leaveBalanceService.checkSufficientLeaveBalance(
                    leaveRequest.getEmployeeId(),
                    leaveRequest.getLeaveType(),
                    calculatedDays
            );
        } catch (LeaveBalanceException e) {
            throw new LeaveBalanceException("Cannot create leave request: " + e.getMessage());
        }

        LeaveRequest savedLeaveRequest = leaveRequestRepository.save(leaveRequest);
        return convertToDto(savedLeaveRequest);
    }

    @Override
    @Transactional
    public LeaveRequestDto updateLeaveRequestStatus(Long leaveId, LeaveRequestStatus status) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        LeaveRequestStatus oldStatus = leaveRequest.getStatus();

        leaveRequest.setStatus(status);
        LeaveRequest updatedLeaveRequest = leaveRequestRepository.save(leaveRequest);

        int calculatedDays = calculateWorkingDays(updatedLeaveRequest.getStartDate(), updatedLeaveRequest.getEndDate());

        if (status == LeaveRequestStatus.APPROVED && oldStatus != LeaveRequestStatus.APPROVED) {
            leaveBalanceService.adjustLeaveBalance(
                    updatedLeaveRequest.getEmployeeId(),
                    updatedLeaveRequest.getLeaveType(),
                    calculatedDays,
                    true
            );
        } else if (status != LeaveRequestStatus.APPROVED && oldStatus == LeaveRequestStatus.APPROVED) {
            leaveBalanceService.adjustLeaveBalance(
                    updatedLeaveRequest.getEmployeeId(),
                    updatedLeaveRequest.getLeaveType(),
                    calculatedDays,
                    false
            );
        }

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
        //TODO: check for employee existence

        return leaveRequestRepository.findByStatusAndEmployeeId(status, employeeId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveRequestResponseDto> getLeaveRequestsByEmployee(Long employeeId) {
        //TODO: check for employee existence

        return leaveRequestRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteLeaveRequest(Long leaveId) {
        LeaveRequest leaveRequest = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new ResourceNotFoundException("Leave request not found with ID: " + leaveId));

        int calculatedDays = calculateWorkingDays(leaveRequest.getStartDate(), leaveRequest.getEndDate());

        if (leaveRequest.getStatus() == LeaveRequestStatus.APPROVED) {
            leaveBalanceService.adjustLeaveBalance(
                    leaveRequest.getEmployeeId(),
                    leaveRequest.getLeaveType(),
                    calculatedDays,
                    false
            );
        }
        leaveRequestRepository.deleteById(leaveId);
    }

    private LeaveRequestDto convertToDto(LeaveRequest leaveRequest) {
        LeaveRequestDto leaveRequestDto = modelMapper.map(leaveRequest, LeaveRequestDto.class);
       return leaveRequestDto;
    }

    private LeaveRequestResponseDto convertToResponseDto(LeaveRequest leaveRequest) {
        LeaveRequestResponseDto responseDto = modelMapper.map(leaveRequest, LeaveRequestResponseDto.class);
        return responseDto;
    }
}