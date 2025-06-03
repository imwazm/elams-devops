package com.cts.leave_management.controller;

import com.cts.leave_management.dto.EmployeeRequestDto;
import com.cts.leave_management.dto.EmployeeResponseDto;
import com.cts.leave_management.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/employees")
public class EmployeeController {

    @Autowired
    EmployeeService employeeService;

    @PostMapping("add-employee")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto addEmployee(@RequestBody EmployeeRequestDto employeeRequestDto){
        return employeeService.addEmployee(employeeRequestDto);
    }

    @PostMapping("add-manager")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto addManager(@RequestBody EmployeeRequestDto employeeRequestDto){
        return employeeService.addManager(employeeRequestDto);
    }

    @PostMapping("add-admin")
    @ResponseStatus(HttpStatus.CREATED)
    public EmployeeResponseDto addAdmin(@RequestBody EmployeeRequestDto employeeRequestDto){
        return employeeService.addAdmin(employeeRequestDto);
    }

    @GetMapping
    public List<EmployeeResponseDto> getAllEmployees(){
        return employeeService.findAllEmployees();
    }

    @GetMapping("{id}")
    public EmployeeResponseDto getEmployeeById(@PathVariable Long id){
        return employeeService.findEmployeeById(id);
    }

    @PostMapping("{id}/promote")
    public EmployeeResponseDto promoteEmployee(@PathVariable Long id){
        return employeeService.promoteEmployee(id);
    }

    @PostMapping("{id}/demote")
    public EmployeeResponseDto demoteEmployee(@PathVariable Long id){
        return employeeService.demoteEmployee(id);
    }

    @PostMapping("{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmployee(@PathVariable Long id){
        employeeService.deleteEmployee(id);
    }

    @PutMapping("{id}/update")
    public EmployeeResponseDto updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequestDto employee){
        return employeeService.updateEmployeeDetails(id, employee);
    }

    @PostMapping("{employeeId}/assign-manager/{managerId}")
    public EmployeeResponseDto assignManager(@PathVariable Long employeeId,
                                             @PathVariable Long managerId){
        return employeeService.assignManager(employeeId, managerId);
    }

    @GetMapping("{managerId}/team-members")
    public List<EmployeeResponseDto> findTeamMembers(@PathVariable Long managerId){
        return employeeService.findTeamMembers(managerId);
    }
}
