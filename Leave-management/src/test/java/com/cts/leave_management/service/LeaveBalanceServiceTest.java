package com.cts.leave_management.service;

import com.cts.leave_management.dto.LeaveBalanceRequestDto;
import com.cts.leave_management.dto.LeaveBalanceResponseDto;
import com.cts.leave_management.entity.LeaveBalance;
import com.cts.leave_management.entity.enums.LeaveType;
import com.cts.leave_management.entity.enums.Role;
import com.cts.leave_management.exception.ResourceNotFoundException;
import com.cts.leave_management.repository.LeaveBalanceRepository;
import feign.FeignException;
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

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private ModelMapper modelMapper;

    private Long employeeId;
    private LeaveBalance casualLeave, sickLeave, vacationLeave;
    private LeaveBalanceRequestDto casualLeaveRequestDto;
    private LeaveBalanceResponseDto casualLeaveResponseDto;
    private LeaveBalanceResponseDto sickLeaveResponseDto;
    private LeaveBalanceResponseDto vacationLeaveResponseDto;

    @BeforeEach
    void setupObject() {
        employeeId = 101L;
        casualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 12, employeeId);
        sickLeave = new LeaveBalance(2L, LeaveType.SICK_LEAVE, 7, employeeId);
        vacationLeave = new LeaveBalance(3L, LeaveType.VACATION_LEAVE, 15, employeeId);

        casualLeaveRequestDto = new LeaveBalanceRequestDto(null, LeaveType.CASUAL_LEAVE, 12, 101L);

        casualLeaveResponseDto = new LeaveBalanceResponseDto(1L, LeaveType.CASUAL_LEAVE, 12, 101L);
        sickLeaveResponseDto = new LeaveBalanceResponseDto(2L, LeaveType.SICK_LEAVE, 7, 101L);
        vacationLeaveResponseDto = new LeaveBalanceResponseDto(3L, LeaveType.VACATION_LEAVE, 15, 101L);
    }

    @Test
    void addLeaveBalance_shouldReturnSavedLeaveBalance() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(eq(employeeId), eq(LeaveType.CASUAL_LEAVE)))
                .thenReturn(Optional.empty());
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(casualLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.addLeaveBalance(casualLeaveRequestDto);

        assertThat(actualDto, is(casualLeaveResponseDto));
        verify(leaveBalanceRepository, times(1)).save(any(LeaveBalance.class));
    }

    @Test
    void addLeaveBalance_shouldUpdateExistingLeaveBalance() {
        LeaveBalance existingCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 10, employeeId);
        LeaveBalance updatedCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 12, employeeId);
        LeaveBalanceResponseDto updatedResponseDto = new LeaveBalanceResponseDto(1L, LeaveType.CASUAL_LEAVE, 12, 101L);

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(eq(employeeId), eq(LeaveType.CASUAL_LEAVE)))
                .thenReturn(Optional.of(existingCasualLeave));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedCasualLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.addLeaveBalance(casualLeaveRequestDto);

        assertThat(actualDto, is(updatedResponseDto));
        verify(leaveBalanceRepository, times(1)).save(existingCasualLeave);
        assertEquals(12, existingCasualLeave.getBalance());
    }

    @Test
    void addLeaveBalance_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {

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

        when(leaveBalanceRepository.findByEmployeeId(employeeId)).thenReturn(employeeBalances);

        List<LeaveBalanceResponseDto> actualDtos = leaveBalanceService.findLeaveBalancesByEmployeeId(employeeId);

        assertThat(actualDtos.size(), is(2));
        assertThat(actualDtos, containsInAnyOrder(casualLeaveResponseDto, sickLeaveResponseDto));
    }

    @Test
    void findLeaveBalancesByEmployeeId_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        Long nonExistentEmployeeId = 999L;

        FeignException exception = assertThrows(FeignException.class,
                () -> leaveBalanceService.findLeaveBalancesByEmployeeId(nonExistentEmployeeId));

        assertThat(exception.getMessage(), containsString("Employee with id " + nonExistentEmployeeId + " not found"));
        verify(leaveBalanceRepository, never()).findByEmployeeId(anyLong());
    }

    @Test
    void updateLeaveBalance_shouldReturnUpdatedLeaveBalance() {
        Long id = 1L;
        LeaveBalance existingLeaveBalance = new LeaveBalance(id, LeaveType.CASUAL_LEAVE, 12, employeeId);
        LeaveBalanceRequestDto updateRequestDto = new LeaveBalanceRequestDto(id, LeaveType.CASUAL_LEAVE, 10, employeeId);
        LeaveBalance updatedLeaveBalanceEntity = new LeaveBalance(id, LeaveType.CASUAL_LEAVE, 10, employeeId);
        LeaveBalanceResponseDto expectedResponseDto = new LeaveBalanceResponseDto(id, LeaveType.CASUAL_LEAVE, 10, employeeId);

        when(leaveBalanceRepository.findById(id)).thenReturn(Optional.of(existingLeaveBalance));
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
        LeaveBalance existingLeaveBalance = new LeaveBalance(id, LeaveType.CASUAL_LEAVE, 12, employeeId);
        LeaveBalanceRequestDto updateRequestDto = new LeaveBalanceRequestDto(id, LeaveType.CASUAL_LEAVE, 10, 999L);

        when(leaveBalanceRepository.findById(id)).thenReturn(Optional.of(existingLeaveBalance));

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.updateLeaveBalance(id, updateRequestDto));

        assertThat(exception.getMessage(), containsString("Employee with id 999 not found"));
    }


    @Test
    void adjustLeaveBalance_shouldDeductDays_whenApproved() {
        LeaveBalance initialCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 12, employeeId);
        LeaveBalance updatedCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 10, employeeId);
        LeaveBalanceResponseDto expectedResponse = new LeaveBalanceResponseDto(1L, LeaveType.CASUAL_LEAVE, 10, employeeId);

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, LeaveType.CASUAL_LEAVE))
                .thenReturn(Optional.of(initialCasualLeave));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedCasualLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.adjustLeaveBalance(employeeId, LeaveType.CASUAL_LEAVE, 2, true);

        assertThat(actualDto, is(expectedResponse));
        assertEquals(10, initialCasualLeave.getBalance());
        verify(leaveBalanceRepository, times(1)).save(initialCasualLeave);
    }

    @Test
    void adjustLeaveBalance_shouldAddDaysBack_whenRejected() {
        LeaveBalance initialSickLeave = new LeaveBalance(2L, LeaveType.SICK_LEAVE, 7, employeeId);
        LeaveBalance updatedSickLeave = new LeaveBalance(2L, LeaveType.SICK_LEAVE, 9, employeeId);
        LeaveBalanceResponseDto expectedResponse = new LeaveBalanceResponseDto(2L, LeaveType.SICK_LEAVE, 9, employeeId);

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, LeaveType.SICK_LEAVE))
                .thenReturn(Optional.of(initialSickLeave));
        when(leaveBalanceRepository.save(any(LeaveBalance.class))).thenReturn(updatedSickLeave);

        LeaveBalanceResponseDto actualDto = leaveBalanceService.adjustLeaveBalance(employeeId, LeaveType.SICK_LEAVE, 2, false);

        assertThat(actualDto, is(expectedResponse));
        assertEquals(9, initialSickLeave.getBalance());
        verify(leaveBalanceRepository, times(1)).save(initialSickLeave);
    }

    @Test
    void adjustLeaveBalance_shouldThrowIllegalArgumentException_whenInsufficientBalance() {
        LeaveBalance initialCasualLeave = new LeaveBalance(1L, LeaveType.CASUAL_LEAVE, 2, employeeId);

        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, LeaveType.CASUAL_LEAVE))
                .thenReturn(Optional.of(initialCasualLeave));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> leaveBalanceService.adjustLeaveBalance(employeeId, LeaveType.CASUAL_LEAVE, 5, true));

        assertThat(exception.getMessage(), containsString("Insufficient leave balance for CASUAL_LEAVE"));
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

    @Test
    void adjustLeaveBalance_shouldThrowResourceNotFoundException_whenEmployeeNotFound() {
        Long nonExistentEmployeeId = 999L;

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.adjustLeaveBalance(nonExistentEmployeeId, LeaveType.CASUAL_LEAVE, 1, true));

        assertThat(exception.getMessage(), containsString("Employee with id " + nonExistentEmployeeId + " not found"));
    }

    @Test
    void adjustLeaveBalance_shouldThrowResourceNotFoundException_whenLeaveBalanceTypeNotFoundForEmployee() {
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(employeeId, LeaveType.PATERNITY_LEAVE))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.adjustLeaveBalance(employeeId, LeaveType.PATERNITY_LEAVE, 1, true));

        assertThat(exception.getMessage(), containsString("Leave balance of type PATERNITY_LEAVE not found for employee " + employeeId));
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
       when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(eq(newEmployeeId), any(LeaveType.class)))
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
        LeaveBalance existingCasualLeave = new LeaveBalance(4L, LeaveType.CASUAL_LEAVE, 5, newEmployeeId);

       when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(eq(newEmployeeId), eq(LeaveType.CASUAL_LEAVE)))
                .thenReturn(Optional.of(existingCasualLeave));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(eq(newEmployeeId), eq(LeaveType.SICK_LEAVE)))
                .thenReturn(Optional.empty());
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveType(eq(newEmployeeId), eq(LeaveType.VACATION_LEAVE)))
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

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> leaveBalanceService.initializeLeaveBalancesForNewEmployee(nonExistentEmployeeId));

        assertThat(exception.getMessage(), containsString("Employee with id " + nonExistentEmployeeId + " not found"));
        verify(leaveBalanceRepository, never()).save(any(LeaveBalance.class));
    }

}