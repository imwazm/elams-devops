package com.cts.leave_management.controller;

import com.cts.leave_management.dto.LeaveBalanceRequestDto;
import com.cts.leave_management.dto.LeaveBalanceResponseDto;
import com.cts.leave_management.entity.enums.LeaveType;
import com.cts.leave_management.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/leave-balances")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveBalanceResponseDto addLeaveBalance(@RequestBody LeaveBalanceRequestDto leaveBalanceRequestDto) {
        return leaveBalanceService.addLeaveBalance(leaveBalanceRequestDto);
    }

    @GetMapping
    public List<LeaveBalanceResponseDto> getAllLeaveBalances() {
        return leaveBalanceService.findAllLeaveBalances();
    }


    @GetMapping("{id}")
    public LeaveBalanceResponseDto getLeaveBalanceById(@PathVariable Long id) {
        return leaveBalanceService.findLeaveBalanceById(id);
    }


    @GetMapping("/employee/{employeeId}")
    public List<LeaveBalanceResponseDto> getLeaveBalancesByEmployeeId(@PathVariable Long employeeId) {
        return leaveBalanceService.findLeaveBalancesByEmployeeId(employeeId);
    }


    @PutMapping("{id}")
    public LeaveBalanceResponseDto updateLeaveBalance(@PathVariable Long id, @RequestBody LeaveBalanceRequestDto leaveBalanceRequestDto) {
        return leaveBalanceService.updateLeaveBalance(id, leaveBalanceRequestDto);
    }


    @PatchMapping("/adjust")
    public LeaveBalanceResponseDto adjustLeaveBalance(
            @RequestParam Long employeeId,
            @RequestParam LeaveType leaveType,
            @RequestParam int days,
            @RequestParam boolean isApproved) {
        return leaveBalanceService.adjustLeaveBalance(employeeId, leaveType, days, isApproved);
    }


    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLeaveBalance(@PathVariable Long id) {
        leaveBalanceService.deleteLeaveBalance(id);
    }

    @PostMapping("/initialize/{employeeId}")
    @ResponseStatus(HttpStatus.OK)
    public void initializeLeaveBalancesForNewEmployee(@PathVariable Long employeeId) {
        leaveBalanceService.initializeLeaveBalancesForNewEmployee(employeeId);
    }
}