package com.cts.leave_management.service;

import com.cts.leave_management.dto.EmployeeRequestDto;
import com.cts.leave_management.dto.EmployeeResponseDto;

import java.util.List;

public interface EmployeeService {

    EmployeeResponseDto addEmployee(EmployeeRequestDto employee);

    EmployeeResponseDto addManager(EmployeeRequestDto employeeDto);

    EmployeeResponseDto addAdmin(EmployeeRequestDto employeeDto);

    List<EmployeeResponseDto> findAllEmployees();

    EmployeeResponseDto findEmployeeById(Long id);

    EmployeeResponseDto promoteEmployee(Long id);

    EmployeeResponseDto demoteEmployee(Long id);

    void deleteEmployee(Long id);

    EmployeeResponseDto updateEmployeeDetails(Long id, EmployeeRequestDto employeeDto);

    EmployeeResponseDto assignManager(Long employeeId, Long managerId);

    List<EmployeeResponseDto> findTeamMembers(Long managerId);
}
