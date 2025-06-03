package com.cts.attendance_management.service;

import com.cts.attendance_management.dto.LeaveBalanceRequestDto;
import com.cts.attendance_management.dto.LeaveBalanceResponseDto;
import com.cts.attendance_management.entity.Employee;
import com.cts.attendance_management.entity.LeaveBalance;
import com.cts.attendance_management.entity.enums.LeaveType;
import com.cts.attendance_management.entity.enums.Role;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.repository.EmployeeRepository;
import com.cts.attendance_management.repository.LeaveBalanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class LeaveBalanceServiceTest {

    @MockitoBean
    private LeaveBalanceRepository leaveBalanceRepository;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private ModelMapper modelMapper;

    private Employee employee1;
    private LeaveBalance casualLeave, sickLeave, vacationLeave;
    private LeaveBalanceRequestDto casualLeaveRequestDto;
    private LeaveBalanceResponseDto casualLeaveResponseDto;
    private LeaveBalanceResponseDto sickLeaveResponseDto;
    private LeaveBalanceResponseDto vacationLeaveResponseDto;

    @BeforeEach
    void setupObject() {
        employee1 = new Employee(101L, "John Doe", "john.doe@example.com", Role.EMPLOYEE, null, null, null);

        casualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 12, employee1);
        sickLeave = new LeaveBalance(2L, LeaveType.SICK_LEAVE, 7, employee1);
        vacationLeave = new LeaveBalance(3L, LeaveType.VACATION_LEAVE, 15, employee1);

        casualLeaveRequestDto = new LeaveBalanceRequestDto(null, LeaveType.CASUAL_LEAVE, 12, 101L);

        casualLeaveResponseDto = new LeaveBalanceResponseDto(1L, LeaveType.CASUAL_LEAVE, 12, 101L, "John Doe");
        sickLeaveResponseDto = new LeaveBalanceResponseDto(2L, LeaveType.SICK_LEAVE, 7, 101L, "John Doe");
        vacationLeaveResponseDto = new LeaveBalanceResponseDto(3L, LeaveType.VACATION_LEAVE, 15, 101L, "John Doe");
    }

    @Test
    void addLeaveBalance_shouldReturnSavedLeaveBalance() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(eq(employee1), eq(LeaveType.CASUAL_LEAVE)))
                .thenReturn(Optional.empty());
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(casualLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.addLeaveBalance(casualLeaveRequestDto);

        assertThat(actualDto, is(casualLeaveResponseDto));
        verify(leaveBalanceRepository, times(1)).save(any(LeaveBalance.class));
    }

    @Test
    void addLeaveBalance_shouldUpdateExistingLeaveBalance() {
        LeaveBalance existingCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 10, employee1);
        LeaveBalance updatedCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 12, employee1);
        LeaveBalanceResponseDto updatedResponseDto = new LeaveBalanceResponseDto(1L, LeaveType.CASUAL_LEAVE, 12, 101L, "John Doe");

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(eq(employee1), eq(LeaveType.CASUAL_LEAVE)))
                .thenReturn(Optional.of(existingCasualLeave));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedCasualLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.addLeaveBalance(casualLeaveRequestDto);

        assertThat(actualDto, is(updatedResponseDto));
        verify(leaveBalanceRepository, times(1)).save(existingCasualLeave);
        assertEquals(12, existingCasualLeave.getBalance());
    }

    @Test
    void addLeaveBalance_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.addLeaveBalance(casualLeaveRequestDto));

        assertThat(exception.getMessage(), containsString("Employee with id " + casualLeaveRequestDto.getEmployeeId() + " not found"));
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    void findAllLeaveBalances_shouldReturnListOfAllBalances() {
        List<LeaveBalance> allBalances = Arrays.asList(casualLeave, sickLeave);
        List<LeaveBalanceResponseDto> expectedDtos = Arrays.asList(casualLeaveResponseDto, sickLeaveResponseDto);

        when(leaveBalanceRepository.findAll()).thenReturn(allBalances);

        List<LeaveBalanceResponseDto> actualDtos = leaveBalanceService.findAllLeaveBalances();

        assertThat(actualDtos.size(), is(2));
        assertThat(actualDtos, containsInAnyOrder(casualLeaveResponseDto, sickLeaveResponseDto));
    }

    @Test
    void findLeaveBalanceById_shouldReturnLeaveBalance_whenFound() {
        Long id = 1L;
        when(leaveBalanceRepository.findById(id)).thenReturn(Optional.of(casualLeave));

        LeaveBalanceResponseDto actualDto = leaveBalanceService.findLeaveBalanceById(id);

        assertThat(actualDto, is(casualLeaveResponseDto));
    }

    @Test
    void findLeaveBalanceById_shouldThrowResourceNotFoundException_whenNotFound() {
        Long id = 99L;
        when(leaveBalanceRepository.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.findLeaveBalanceById(id));

        assertThat(exception.getMessage(), containsString("Leave balance with id " + id + " not found"));
    }

    @Test
    void findLeaveBalancesByEmployeeId_shouldReturnListOfBalancesForEmployee() {
        List<LeaveBalance> employeeBalances = Arrays.asList(casualLeave, sickLeave);
        List<LeaveBalanceResponseDto> expectedDtos = Arrays.asList(casualLeaveResponseDto, sickLeaveResponseDto);

        when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployee(employee1)).thenReturn(employeeBalances);

        List<LeaveBalanceResponseDto> actualDtos = leaveBalanceService.findLeaveBalancesByEmployeeId(employee1.getId());

        assertThat(actualDtos.size(), is(2));
        assertThat(actualDtos, containsInAnyOrder(casualLeaveResponseDto, sickLeaveResponseDto));
    }

    @Test
    void findLeaveBalancesByEmployeeId_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        Long nonExistentEmployeeId = 999L;
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.findLeaveBalancesByEmployeeId(nonExistentEmployeeId));

        assertThat(exception.getMessage(), containsString("Employee with id " + nonExistentEmployeeId + " not found"));
        verify(leaveBalanceRepository, never()).findByEmployee(any(Employee.class));
    }

    @Test
    void updateLeaveBalance_shouldReturnUpdatedLeaveBalance() {
        Long id = 1L;
        LeaveBalance existingLeaveBalance = new LeaveBalance(id, LeaveType.CASUAL_LEAVE, 12, employee1);
        LeaveBalanceRequestDto updateRequestDto = new LeaveBalanceRequestDto(id, LeaveType.CASUAL_LEAVE, 10, employee1.getId());
        LeaveBalance updatedLeaveBalanceEntity = new LeaveBalance(id, LeaveType.CASUAL_LEAVE, 10, employee1);
        LeaveBalanceResponseDto expectedResponseDto = new LeaveBalanceResponseDto(id, LeaveType.CASUAL_LEAVE, 10, employee1.getId(), employee1.getEmployeeName());

        when(leaveBalanceRepository.findById(id)).thenReturn(Optional.of(existingLeaveBalance));
        when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedLeaveBalanceEntity);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.updateLeaveBalance(id, updateRequestDto);

        assertThat(actualDto, is(expectedResponseDto));
        verify(leaveBalanceRepository, times(1)).save(existingLeaveBalance);
        assertEquals(10, existingLeaveBalance.getBalance());
    }

    @Test
    void updateLeaveBalance_shouldThrowResourceNotFoundException_whenLeaveBalanceNotFound() {
        Long nonExistentId = 99L;
        when(leaveBalanceRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.updateLeaveBalance(nonExistentId, casualLeaveRequestDto));

        assertThat(exception.getMessage(), containsString("Leave balance with id " + nonExistentId + " not found"));
    }

    @Test
    void updateLeaveBalance_shouldThrowResourceNotFoundException_whenEmployeeNotFoundForUpdate() {
        Long id = 1L;
        LeaveBalance existingLeaveBalance = new LeaveBalance(id, LeaveType.CASUAL_LEAVE, 12, employee1);
        LeaveBalanceRequestDto updateRequestDto = new LeaveBalanceRequestDto(id, LeaveType.CASUAL_LEAVE, 10, 999L);

        when(leaveBalanceRepository.findById(id)).thenReturn(Optional.of(existingLeaveBalance));
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.updateLeaveBalance(id, updateRequestDto));

        assertThat(exception.getMessage(), containsString("Employee with id 999 not found"));
    }


    @Test
    void adjustLeaveBalance_shouldDeductDays_whenApproved() {
        LeaveBalance initialCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 12, employee1);
        LeaveBalance updatedCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 10, employee1);
        LeaveBalanceResponseDto expectedResponse = new LeaveBalanceResponseDto(1L, LeaveType.CASUAL_LEAVE, 10, employee1.getId(), employee1.getEmployeeName());

        when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee1, LeaveType.CASUAL_LEAVE))
                .thenReturn(Optional.of(initialCasualLeave));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedCasualLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.adjustLeaveBalance(employee1.getId(), LeaveType.CASUAL_LEAVE, 2, true);

        assertThat(actualDto, is(expectedResponse));
        assertEquals(10, initialCasualLeave.getBalance());
        verify(leaveBalanceRepository, times(1)).save(initialCasualLeave);
    }

    @Test
    void adjustLeaveBalance_shouldAddDaysBack_whenRejected() {
        LeaveBalance initialSickLeave = new LeaveBalance(2L, LeaveType.SICK_LEAVE, 7, employee1);
        LeaveBalance updatedSickLeave = new LeaveBalance(2L, LeaveType.SICK_LEAVE, 9, employee1);
        LeaveBalanceResponseDto expectedResponse = new LeaveBalanceResponseDto(2L, LeaveType.SICK_LEAVE, 9, employee1.getId(), employee1.getEmployeeName());

        when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee1, LeaveType.SICK_LEAVE))
                .thenReturn(Optional.of(initialSickLeave));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedSickLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.adjustLeaveBalance(employee1.getId(), LeaveType.SICK_LEAVE, 2, false);

        assertThat(actualDto, is(expectedResponse));
        assertEquals(9, initialSickLeave.getBalance());
        verify(leaveBalanceRepository, times(1)).save(initialSickLeave);
    }

    @Test
    void adjustLeaveBalance_shouldThrowIllegalArgumentException_whenInsufficientBalance() {
        LeaveBalance initialCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 2, employee1);

        when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee1, LeaveType.CASUAL_LEAVE))
                .thenReturn(Optional.of(initialCasualLeave));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> leaveBalanceService.adjustLeaveBalance(employee1.getId(), LeaveType.CASUAL_LEAVE, 5, true));

        assertThat(exception.getMessage(), containsString("Insufficient leave balance for CASUAL_LEAVE"));
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    void adjustLeaveBalance_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        Long nonExistentEmployeeId = 999L;
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.adjustLeaveBalance(nonExistentEmployeeId, LeaveType.CASUAL_LEAVE, 1, true));

        assertThat(exception.getMessage(), containsString("Employee with id " + nonExistentEmployeeId + " not found"));
    }

    @Test
    void adjustLeaveBalance_shouldThrowResourceNotFoundException_whenLeaveBalanceTypeNotFoundForEmployee() {
        when(employeeRepository.findById(employee1.getId())).thenReturn(Optional.of(employee1));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(employee1, LeaveType.PATERNITY_LEAVE))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.adjustLeaveBalance(employee1.getId(), LeaveType.PATERNITY_LEAVE, 1, true));

        assertThat(exception.getMessage(), containsString("Leave balance of type PATERNITY_LEAVE not found for employee " + employee1.getId()));
    }

    @Test
    void deleteLeaveBalance_shouldRemoveLeaveBalance() {
        Long id = 1L;
        when(leaveBalanceRepository.existsById(id)).thenReturn(true);
        doNothing().when(leaveBalanceRepository).deleteById(id);

        leaveBalanceService.deleteLeaveBalance(id);

        verify(leaveBalanceRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteLeaveBalance_shouldThrowResourceNotFoundException_whenNotFound() {
        Long id = 99L;
        when(leaveBalanceRepository.existsById(id)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.deleteLeaveBalance(id));

        assertThat(exception.getMessage(), containsString("Leave balance with id " + id + " not found for deletion"));
        verify(leaveBalanceRepository, never()).deleteById(anyLong());
    }

    @Test
    void initializeLeaveBalancesForNewEmployee_shouldCreateAllDefaultBalances() {
        Long newEmployeeId = 102L;
        Employee newEmployee = new Employee(newEmployeeId, "New Employee", "new@example.com", Role.EMPLOYEE, null, null, null);

        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(eq(newEmployee), any(LeaveType.class)))
                .thenReturn(Optional.empty());
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenAnswer(invocation -> {
            LeaveBalance lb = invocation.getArgument(0);
            if (lb.getId() == null) lb.setId(100L + (int) (Math.random() * 100));
            return lb;
        });

        leaveBalanceService.initializeLeaveBalancesForNewEmployee(newEmployeeId);

        verify(leaveBalanceRepository, times(LeaveType.values().length)).save(any(LeaveBalance.class));
    }

    @Test
    void initializeLeaveBalancesForNewEmployee_shouldNotOverwriteExistingBalances() {
        Long newEmployeeId = 102L;
        Employee newEmployee = new Employee(newEmployeeId, "New Employee", "new@example.com", Role.EMPLOYEE, null, null, null);
        LeaveBalance existingCasualLeave = new LeaveBalance(4L, LeaveType.CASUAL_LEAVE, 5, newEmployee);

        when(employeeRepository.findById(newEmployeeId)).thenReturn(Optional.of(newEmployee));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(eq(newEmployee), eq(LeaveType.CASUAL_LEAVE)))
                .thenReturn(Optional.of(existingCasualLeave));
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(eq(newEmployee), eq(LeaveType.SICK_LEAVE)))
                .thenReturn(Optional.empty());
        when(leaveBalanceRepository.findByEmployeeAndLeaveType(eq(newEmployee), eq(LeaveType.VACATION_LEAVE)))
                .thenReturn(Optional.empty());
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        leaveBalanceService.initializeLeaveBalancesForNewEmployee(newEmployeeId);

        verify(leaveBalanceRepository, times(LeaveType.values().length - 1)).save(any(LeaveBalance.class));
        verify(leaveBalanceRepository, never()).save(argThat(lb -> lb.getLeaveType() == LeaveType.CASUAL_LEAVE && lb.getId().equals(existingCasualLeave.getId())));
        assertEquals(5, existingCasualLeave.getBalance());
    }

    @Test
    void initializeLeaveBalancesForNewEmployee_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        Long nonExistentEmployeeId = 999L;
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.initializeLeaveBalancesForNewEmployee(nonExistentEmployeeId));

        assertThat(exception.getMessage(), containsString("Employee with id " + nonExistentEmployeeId + " not found"));
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

}