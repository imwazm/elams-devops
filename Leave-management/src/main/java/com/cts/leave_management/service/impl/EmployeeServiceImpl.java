package com.cts.leave_management.service.impl;

import com.cts.leave_management.dto.EmployeeRequestDto;
import com.cts.leave_management.dto.EmployeeResponseDto;
import com.cts.leave_management.entity.Employee;
import com.cts.leave_management.entity.enums.Role;
import com.cts.leave_management.exception.InvalidRoleException;
import com.cts.leave_management.exception.ResourceNotFoundException;
import com.cts.leave_management.repository.EmployeeRepository;
import com.cts.leave_management.service.EmployeeService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Override
    public EmployeeResponseDto addEmployee(EmployeeRequestDto employeeDto) {
        Employee newEmployee = modelMapper.map(employeeDto, Employee.class);
        newEmployee.setRole(Role.EMPLOYEE);
        Employee savedEmployee = employeeRepository.save(newEmployee);
        logger.info("New employee added with ID: " + savedEmployee.getId());
        return modelMapper.map(savedEmployee, EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto addManager(EmployeeRequestDto employeeDto) {
        Employee newEmployee = modelMapper.map(employeeDto, Employee.class);
        newEmployee.setRole(Role.MANAGER);
        Employee savedEmployee = employeeRepository.save(newEmployee);
        logger.info("New manager added with ID: " + savedEmployee.getId());
        return modelMapper.map(savedEmployee, EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto addAdmin(EmployeeRequestDto employeeDto) {
        Employee newEmployee = modelMapper.map(employeeDto, Employee.class);
        newEmployee.setRole(Role.ADMIN);
        Employee savedEmployee = employeeRepository.save(newEmployee);
        logger.info("New admin added with ID: " + savedEmployee.getId());
        return modelMapper.map(savedEmployee, EmployeeResponseDto.class);
    }

    @Override
    public List<EmployeeResponseDto> findAllEmployees() {
        logger.info("Fetching all employees");
        return employeeRepository.findAll()
                .stream().map((element) -> modelMapper.map(element, EmployeeResponseDto.class))
                .toList();
    }

    @Override
    public EmployeeResponseDto findEmployeeById(Long id) {
        logger.info("Fetching employee with ID: " + id);
        Employee employee = this.findEmployeeByIdHelper(id);
        if (employee == null) {
            logger.error("Employee with ID " + id + " not found");
            throw new ResourceNotFoundException("Employee with id " + id + " not found");
        }
        logger.info("Employee found: " + employee.getEmployeeName());
        logger.debug("Employee details: " + employee);
        return modelMapper.map(employee, EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto promoteEmployee(Long id) {
        logger.info("Promoting employee with ID: " + id);
        Employee employee = this.findEmployeeByIdHelper(id);
        if(employee.getRole() == Role.ADMIN){
            logger.error("Cannot promote employee with ID " + id + ": already an Admin");
            throw new InvalidRoleException("Employee with "+id+" is already an Admin");
        }
        if(employee.getRole() == Role.MANAGER){
            logger.error("Cannot promote employee with ID " + id + ": already a Manager");
            throw new InvalidRoleException("Employee with "+id+" is already a Manager");
        }
        logger.info("Employee with ID " + id + " promoted to Manager");
        employee.setRole(Role.MANAGER);
        employeeRepository.save(employee);
        logger.debug("Updated employee details after promotion: " + employee);
        logger.info("Employee with ID " + id + " successfully promoted to Manager");
        return modelMapper.map(employee, EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto demoteEmployee(Long id) {
        logger.info("Demoting employee with ID: " + id);
        Employee employee = this.findEmployeeByIdHelper(id);
        if(employee.getRole() == Role.ADMIN){
            logger.error("Cannot demote employee with ID " + id + ": already an Admin");
            throw new InvalidRoleException("Employee with "+id+" is already an Admin");
        }
        if(employee.getRole() == Role.EMPLOYEE){
            logger.error("Cannot demote employee with ID " + id + ": already an Employee");
            throw new InvalidRoleException("Employee with "+id+" is already an Employee");
        }
        employee.setRole(Role.EMPLOYEE);
        employeeRepository.save(employee);
        logger.info("Employee with ID " + id + " successfully demoted to Employee");
        return modelMapper.map(employee, EmployeeResponseDto.class);
    }

    @Override
    public void deleteEmployee(Long id) {
        logger.info("Deleting employee with ID: " + id);
        Employee employee = findEmployeeByIdHelper(id);
        employeeRepository.deleteById(employee.getId());
        logger.info("Employee with ID " + id + " deleted successfully");
    }

    @Override
    public EmployeeResponseDto updateEmployeeDetails(Long id, EmployeeRequestDto employeeDto) {
        logger.info("Updating details for employee with ID: " + id);
        Employee employee = findEmployeeByIdHelper(id);
        employee.setEmployeeName(employeeDto.getEmployeeName());
        employee.setEmail(employeeDto.getEmail());
        employee = employeeRepository.save(employee);
        logger.info("Employee with ID " + id + " updated successfully");
        return modelMapper.map(employee, EmployeeResponseDto.class);
    }

    @Override
    public EmployeeResponseDto assignManager(Long employeeId, Long managerId) {
        logger.info("Assigning manager with ID: " + managerId + " to employee with ID: " + employeeId);
        Employee employee = findEmployeeByIdHelper(employeeId);
        if (employee.getRole() != Role.EMPLOYEE) {
            logger.error("Cannot assign a manager to employee with ID " + employeeId + ": Current role is " + employee.getRole());
            throw new InvalidRoleException(
                    "Cannot assign a manager to employee with ID " + employeeId
                            + ": Only Employees can have managers assigned. Current role is "
                            + employee.getRole());
        }

        Employee manager = findEmployeeByIdHelper(managerId);
        employee.setManager(manager);
        Employee savedEmployee = employeeRepository.save(employee);
        logger.info("Manager with ID " + managerId + " assigned to employee with ID " + employeeId);
        return modelMapper.map(savedEmployee, EmployeeResponseDto.class);
    }

    @Override
    public List<EmployeeResponseDto> findTeamMembers(Long managerId) {
        logger.info("Fetching team members for manager with ID: " + managerId);
        Employee manager = this.findEmployeeByIdHelper(managerId);
        if(manager.getRole()!=Role.MANAGER){
            logger.error("Employee with ID " + managerId + " is not a Manager");
            throw new InvalidRoleException("Employee with id "+managerId+" is not a Manager");
        }
        List<Employee> teamMembers = manager.getTeamMembers();
        return teamMembers.stream()
                .map((element) -> modelMapper.map(element, EmployeeResponseDto.class))
                .toList();
    }

    private Employee findEmployeeByIdHelper(Long id){
        logger.info("Searching for employee with ID: " + id);
        return employeeRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Employee with id "+id+" not found")
        );
    }
}
