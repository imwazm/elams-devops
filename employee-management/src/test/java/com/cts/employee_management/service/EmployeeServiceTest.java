package com.cts.employee_management.service;

import com.cts.employee_management.dto.EmployeeRequestDto;
import com.cts.employee_management.dto.EmployeeResponseDto;
import com.cts.employee_management.entity.Employee;
import com.cts.employee_management.entity.enums.Role;
import com.cts.employee_management.exception.InvalidRoleException;
import com.cts.employee_management.exception.ResourceNotFoundException;
import com.cts.employee_management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
public class EmployeeServiceTest {

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    private EmployeeRequestDto employeeRequestDto;

    private EmployeeResponseDto expectedEmployeeResponseDto,
            expectedManagerResponseDto, expectedAdminResponseDto;

    private  Employee newEmployee, newManager, newAdmin,
            savedEmployee, savedManager, savedAdmin;

    private Employee employee1, employee2, employee3;
    private List<Employee> employeeList = new LinkedList<>();

    private EmployeeRequestDto employeeDto1, employeeDto2, employeeDto3;
    private List<EmployeeResponseDto> employeeResponseDtoList = new LinkedList<>();

    @BeforeEach
    void setupObject(){
        employeeRequestDto = new EmployeeRequestDto("Akram", "akram@sample.com");
        newEmployee = new Employee(null, "Akram","akram@sample.com", Role.EMPLOYEE,
                null, null, null);
        savedEmployee = new Employee(1L, "Akram","akram@sample.com", Role.EMPLOYEE,
                null, null, null);
        newManager = new Employee(null, "Akram", "akram@sample.com",Role.MANAGER,
                null, null, null);
        savedManager = new Employee(2L, "Akram","akram@sample.com", Role.MANAGER,
                null, null, null);
        newAdmin = new Employee(null, "Akram", "akram@sample.com",Role.ADMIN,
                null, null, null);
        savedAdmin = new Employee(3L, "Akram","akram@sample.com", Role.ADMIN,
                null, null, null);
        expectedEmployeeResponseDto = new EmployeeResponseDto(1L, "Akram","akram@sample.com",
                Role.EMPLOYEE, null, null);
        expectedManagerResponseDto = new EmployeeResponseDto(2L, "Akram","akram@sample.com",
                Role.MANAGER, null, null);
        expectedAdminResponseDto = new EmployeeResponseDto(3L, "Akram","akram@sample.com",
                Role.ADMIN, null, null);

        employee1 = new Employee(null, "Akram","akram@sample.com", Role.EMPLOYEE,
                null, null, null);
        employee2 = new Employee(null, "Piyush","Piyush@sample.com", Role.MANAGER,
                null, null, null);
        employee3 = new Employee(null, "Rohit","Rohit@sample.com", Role.ADMIN,
                null, null, null);

        employeeList.add(new Employee(1L, "Akram", "akram@sample.com",Role.EMPLOYEE,
                null, null, null));
        employeeList.add(new Employee(2L, "Piyush","Piyush@sample.com", Role.MANAGER,
                null, null, null));
        employeeList.add(new Employee(3L, "Rohit","Rohit@sample.com", Role.ADMIN,
                null, null, null));

        employeeDto1 = new EmployeeRequestDto("Akram","akram@sample.com");
        employeeDto2 = new EmployeeRequestDto("Piyush", "Piyush@sample.com");
        employeeDto3 = new EmployeeRequestDto("Rohit","Rohit@sample.com");

        employeeResponseDtoList.add(new EmployeeResponseDto(1L, "Akram","akram@sample.com",
                Role.EMPLOYEE, null, null));
        employeeResponseDtoList.add(new EmployeeResponseDto(2L, "Piyush","Piyush@sample.com",
                Role.MANAGER, null, null));
        employeeResponseDtoList.add(new EmployeeResponseDto(3L, "Rohit", "Rohit@sample.com",
                Role.ADMIN, null, null));
    }


    @Test
    void addEmployee_shouldReturnSavedEmployee(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponseDto ActualEmployeeDto = employeeService.addEmployee(employeeRequestDto);

        assertEquals(expectedEmployeeResponseDto, ActualEmployeeDto);
    }

    @Test
    void addManager_shouldReturnSavedManager(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedManager);

        EmployeeResponseDto ActualEmployeeDto = employeeService.addManager(employeeRequestDto);

        assertEquals(expectedManagerResponseDto, ActualEmployeeDto);
    }

    @Test
    void addAdmin_shouldReturnSavedAdmin(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedAdmin);

        EmployeeResponseDto ActualEmployeeDto = employeeService.addAdmin(employeeRequestDto);

        assertEquals(expectedAdminResponseDto, ActualEmployeeDto);
    }


    @Test
    void findEmployeeRole_shouldHaveRoleAsEMPLOYEE(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponseDto savedEmployeeResponseDto = employeeService.addEmployee(employeeRequestDto);

        when(employeeRepository.findById(anyLong()))
                .thenReturn(Optional.of(savedEmployee));

        Role atcualRole = savedEmployeeResponseDto.getRole();

        assertThat(atcualRole, is(Role.EMPLOYEE));
    }

    @Test
    void findEmployeeRole_shouldHaveRoleAsMANAGER(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedManager);

        EmployeeResponseDto savedEmployeeResponseDto = employeeService.addManager(employeeRequestDto);
        when(employeeRepository.findById(anyLong()))
                .thenReturn(Optional.of(savedManager));

        Role atcualRole = savedEmployeeResponseDto.getRole();

        assertThat(atcualRole, is(Role.MANAGER));
    }

    @Test
    void findEmployeeRole_shouldHaveRoleAsADMIN(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedAdmin);

        EmployeeResponseDto savedEmployeeResponseDto = employeeService.addAdmin(employeeRequestDto);
        when(employeeRepository.findById(anyLong()))
                .thenReturn(Optional.of(savedAdmin));

        Role atcualRole = savedEmployeeResponseDto.getRole();

        assertThat(atcualRole, is(Role.ADMIN));
    }


    @Test
    void findAllEmployees_shouldGetAllEmployees(){
        when(employeeRepository.findAll())
                .thenReturn(employeeList);

        List<EmployeeResponseDto> actualEmployeeDtoList = employeeService.findAllEmployees();
        assertThat(actualEmployeeDtoList, is(employeeResponseDtoList));
    }

    @Test
    void findEmployeeById_shouldReturnEmployeeById(){
        Long id = 1L;

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedEmployee));

        EmployeeResponseDto savedEmployeeDto = employeeService.addEmployee(employeeRequestDto);

        EmployeeResponseDto actualEmployeeDto = employeeService.findEmployeeById(id);

        assertThat(actualEmployeeDto, is(savedEmployeeDto));
    }

    @Test
    void findEmployeeById_shouldThrowErrorForNotFound(){
        Long id = 99L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(
                ResourceNotFoundException.class,
                ()->employeeService.findEmployeeById(id));

        assertThat(ex.getMessage(), stringContainsInOrder(""+id));
    }

    @Test
    void promoteEmployee_shouldPromoteEmployeeToManager(){
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedEmployee));

        EmployeeResponseDto savedEmployeeResponseDto =  employeeService.promoteEmployee(id);

        assertThat(savedEmployeeResponseDto.getRole(), is(Role.MANAGER));
    }
    @Test
    void promoteEmployee_shouldThrowExceptionIfTheEmployeeIsAlreadyManager(){
        Long id = 2L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedManager));

        assertThrows(InvalidRoleException.class,
                ()->employeeService.promoteEmployee(id));
    }
    @Test
    void demoteEmployee_shouldDemoteManagerToEmployee(){
        Long id = 2L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedManager));

        EmployeeResponseDto savedEmployeeResponseDto =  employeeService.demoteEmployee(id);

        assertThat(savedEmployeeResponseDto.getRole(), is(Role.EMPLOYEE));
    }
    @Test
    void demoteEmployee_shouldThrowExceptionIfTheEmployeeIsAlreadyEmployee(){
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedEmployee));

        assertThrows(InvalidRoleException.class,
                ()->employeeService.demoteEmployee(id));
    }

    @Test
    void promoteEmployee_shouldThrowExceptionIfTheEmployeeIsADMIN(){
        Long id = 3L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedAdmin));

        assertThrows(InvalidRoleException.class,
                ()->employeeService.promoteEmployee(id));
    }

    @Test
    void demoteEmployee_shouldThrowExceptionIfTheEmployeeIsADMIN(){
        Long id = 3L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedAdmin));

        assertThrows(InvalidRoleException.class,
                ()->employeeService.demoteEmployee(id));
    }

    @Test
    void deleteEmployee_shouldRemoveTheEmployee(){
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedEmployee))
                .thenReturn(Optional.empty());

        assertThat(employeeService.findEmployeeById(id), is(expectedEmployeeResponseDto));

        assertThrows(ResourceNotFoundException.class,
                ()->employeeService.deleteEmployee(id));
    }

    @Test
    void updateEmployee_shouldUpdateEmployeeNameAndEmail(){
        Long id = 1L;
        Employee updatedEmployee = new Employee(id, "Arjun", "arjun@sample.com",
                Role.EMPLOYEE, null, null, null);
        EmployeeResponseDto expectedEmployeeDto = new EmployeeResponseDto(id, "Arjun", "arjun@sample.com",
                Role.EMPLOYEE, null, null);

        when(employeeRepository.findById(id)).thenReturn(Optional.of(savedEmployee))
                .thenReturn(Optional.empty());
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        employeeRequestDto.setEmployeeName("Arjun");
        employeeRequestDto.setEmail("arjun@sample.com");
        EmployeeResponseDto actualEmployeeResponseDto =  employeeService.updateEmployeeDetails(id, employeeRequestDto);

        assertThat(actualEmployeeResponseDto, is(expectedEmployeeDto));
    }

    @Test
    void assignManager_shouldAssignManagerToTheEmployee(){
        Long employeeId=1L, managerId=2L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(savedEmployee));
        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(savedManager));
        savedEmployee.setManager(savedManager);
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponseDto actualEmployeeResponseDto =  employeeService.assignManager(employeeId, managerId);
        assertThat(actualEmployeeResponseDto.getManagerId(), is(managerId));
    }

    @Test
    void assignManager_shouldThrowExceptionWhenAssignManagerToManger(){
        Long managerId=2L;
        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(savedManager));

        assertThrows(InvalidRoleException.class,
                () -> employeeService.assignManager(managerId, managerId));
    }

    @Test
    void findTeamMember_managerShouldHaveAssignedEmployeesAsTeamMembers(){
        Long employeeId=1L, managerId=2L;
        savedEmployee.setManager(savedManager);
        savedManager.setTeamMembers(List.of(savedEmployee));
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(savedEmployee));
        when(employeeRepository.findById(managerId)).thenReturn(Optional.of(savedManager));
        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        expectedEmployeeResponseDto.setManagerId(managerId);
        List<EmployeeResponseDto> expectedTeamMembers = new LinkedList<>();
        expectedTeamMembers.add(expectedEmployeeResponseDto);

        employeeService.assignManager(employeeId, managerId);
        List<EmployeeResponseDto> actualTeamMembers = employeeService.findTeamMembers(managerId);
        assertThat(actualTeamMembers, is(expectedTeamMembers));
    }

    @Test
    void findTeamMember_shouldThrowErrorWhenTryFindTeamMemberForNotManager(){
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(savedEmployee));
        assertThrows(InvalidRoleException.class,
                ()->employeeService.findTeamMembers(employeeId));
    }

}