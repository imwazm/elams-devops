package com.cts.leave_management.service.impl;

import com.cts.leave_management.dto.LeaveBalanceRequestDto;
import com.cts.leave_management.dto.LeaveBalanceResponseDto;
import com.cts.leave_management.entity.Employee;
import com.cts.leave_management.entity.LeaveBalance;
import com.cts.leave_management.entity.enums.LeaveType;
import com.cts.leave_management.exception.ResourceNotFoundException;
import com.cts.leave_management.repository.EmployeeRepository;
import com.cts.leave_management.repository.LeaveBalanceRepository;
import com.cts.leave_management.service.LeaveBalanceService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveBalanceServiceImpl implements LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    private static final Logger logger = LoggerFactory.getLogger(LeaveBalanceServiceImpl.class);

    @Override
    @Transactional
    public LeaveBalanceResponseDto addLeaveBalance(LeaveBalanceRequestDto leaveBalanceDto) {
        logger.info("Attempting to add/update leave balance for employee ID: {}", leaveBalanceDto.getEmployeeId());
        Employee employee = employeeRepository.findById(leaveBalanceDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + leaveBalanceDto.getEmployeeId() + " not found"));

        Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveBalanceDto.getLeaveType());

        LeaveBalance leaveBalance;
        if (existingBalance.isPresent()) {
            leaveBalance = existingBalance.get();
            leaveBalance.setBalance(leaveBalanceDto.getBalance());
            logger.warn("Leave balance of type {} already exists for employee {}. Updating balance to {}.", leaveBalanceDto.getLeaveType(), employee.getEmployeeName(), leaveBalanceDto.getBalance());
        } else {
            leaveBalance = modelMapper.map(leaveBalanceDto, LeaveBalance.class);
            leaveBalance.setEmployee(employee);
            logger.info("Creating new leave balance for employee ID: {} with type: {} and balance: {}", employee.getId(), leaveBalanceDto.getLeaveType(), leaveBalanceDto.getBalance());
        }

        LeaveBalance savedLeaveBalance = leaveBalanceRepository.save(leaveBalance);
        logger.info("Leave balance successfully added/updated for employee ID: {} with type: {}", savedLeaveBalance.getEmployee().getId(), savedLeaveBalance.getLeaveType());
        return mapToResponseDto(savedLeaveBalance);
    }

    @Override
    public List<LeaveBalanceResponseDto> findAllLeaveBalances() {
        logger.info("Fetching all leave balances.");
        return leaveBalanceRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveBalanceResponseDto findLeaveBalanceById(Long id) {
        logger.info("Fetching leave balance with ID: {}", id);
        LeaveBalance leaveBalance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance with id " + id + " not found"));
        logger.info("Successfully found leave balance for ID: {}", id);
        return mapToResponseDto(leaveBalance);
    }

    @Override
    public List<LeaveBalanceResponseDto> findLeaveBalancesByEmployeeId(Long employeeId) {
        logger.info("Fetching leave balances for employee ID: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + employeeId + " not found"));
        List<LeaveBalance> leaveBalances = leaveBalanceRepository.findByEmployee(employee);
        logger.info("Found {} leave balances for employee ID: {}", leaveBalances.size(), employeeId);
        return leaveBalances.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LeaveBalanceResponseDto updateLeaveBalance(Long id, LeaveBalanceRequestDto leaveBalanceDto) {
        logger.info("Updating leave balance with ID: {}", id);
        LeaveBalance existingLeaveBalance = leaveBalanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance with id " + id + " not found"));

        Employee employee = employeeRepository.findById(leaveBalanceDto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + leaveBalanceDto.getEmployeeId() + " not found"));

        existingLeaveBalance.setLeaveType(leaveBalanceDto.getLeaveType());
        existingLeaveBalance.setBalance(leaveBalanceDto.getBalance());
        existingLeaveBalance.setEmployee(employee);

        LeaveBalance updatedLeaveBalance = leaveBalanceRepository.save(existingLeaveBalance);
        logger.info("Leave balance with ID: {} updated successfully.", id);
        return mapToResponseDto(updatedLeaveBalance);
    }

    @Override
    @Transactional
    public LeaveBalanceResponseDto adjustLeaveBalance(Long employeeId, LeaveType leaveType, int days, boolean isApproved) {
        logger.info("Adjusting leave balance for employee ID: {}, Leave Type: {}, Days: {}, Approved: {}", employeeId, leaveType, days, isApproved);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + employeeId + " not found"));

        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance of type " + leaveType + " not found for employee " + employeeId));

        if (isApproved) {
            if (leaveBalance.getBalance() < days) {
                logger.error("Insufficient leave balance for employee ID: {} for leave type {}. Requested: {}, Available: {}", employeeId, leaveType, days, leaveBalance.getBalance());
                throw new IllegalArgumentException("Insufficient leave balance for " + leaveType.name() + ". Requested: " + days + ", Available: " + leaveBalance.getBalance());
            }
            leaveBalance.setBalance(leaveBalance.getBalance() - days);
            logger.info("Deducted {} days from {} leave for employee ID: {}. New balance: {}", days, leaveType, employeeId, leaveBalance.getBalance());
        } else {
            leaveBalance.setBalance(leaveBalance.getBalance() + days);
            logger.info("Added back {} days to {} leave for employee ID: {}. New balance: {}", days, leaveType, employeeId, leaveBalance.getBalance());
        }

        LeaveBalance savedLeaveBalance = leaveBalanceRepository.save(leaveBalance);
        return mapToResponseDto(savedLeaveBalance);
    }

    @Override
    public void deleteLeaveBalance(Long id) {
        logger.info("Attempting to delete leave balance with ID: {}", id);
        if (!leaveBalanceRepository.existsById(id)) {
            logger.error("Leave balance with ID {} not found for deletion.", id);
            throw new ResourceNotFoundException("Leave balance with id " + id + " not found for deletion");
        }
        leaveBalanceRepository.deleteById(id);
        logger.info("Leave balance with ID: {} deleted successfully.", id);
    }

    @Override
    @Transactional
    public void initializeLeaveBalancesForNewEmployee(Long employeeId) {
        logger.info("Initializing leave balances for new employee with ID: {}", employeeId);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + employeeId + " not found"));

        List<LeaveType> leaveTypesToInitialize = Arrays.asList(
                LeaveType.CASUAL_LEAVE,
                LeaveType.SICK_LEAVE,
                LeaveType.VACATION_LEAVE,
                LeaveType.PATERNITY_LEAVE,
                LeaveType.COMPENSATORY_OFF,
                LeaveType.LOSS_OF_PAY
        );

        int initialCasualLeave = 12;
        int initialSickLeave = 7;
        int initialVacationLeave = 15;
        int initialPaternityLeave = 5;
        int initialCompOff = 0;
        int initialLossOfPay = 0;

        for (LeaveType type : leaveTypesToInitialize) {
            Optional<LeaveBalance> existingBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, type);
            if (existingBalance.isEmpty()) {
                LeaveBalance newBalance = new LeaveBalance();
                newBalance.setEmployee(employee);
                newBalance.setLeaveType(type);
                switch (type) {
                    case CASUAL_LEAVE:
                        newBalance.setBalance(initialCasualLeave);
                        break;
                    case SICK_LEAVE:
                        newBalance.setBalance(initialSickLeave);
                        break;
                    case VACATION_LEAVE:
                        newBalance.setBalance(initialVacationLeave);
                        break;
                    case PATERNITY_LEAVE:
                        newBalance.setBalance(initialPaternityLeave);
                        break;
                    case COMPENSATORY_OFF:
                        newBalance.setBalance(initialCompOff);
                        break;
                    case LOSS_OF_PAY:
                        newBalance.setBalance(initialLossOfPay);
                        break;
                    default:
                        newBalance.setBalance(0);
                        break;
                }
                leaveBalanceRepository.save(newBalance);
                logger.info("Initialized {} for employee ID: {} with balance: {}", type, employeeId, newBalance.getBalance());
            } else {
                logger.debug("Leave balance for {} already exists for employee ID: {}. Skipping initialization.", type, employeeId);
            }
        }
        logger.info("Finished initializing leave balances for employee ID: {}", employeeId);
    }

    @Override
    public void checkSufficientLeaveBalance(Long employeeId, LeaveType leaveType, int days) {
        logger.info("Checking sufficient leave balance for employee ID: {}, Leave Type: {}, Days: {}", employeeId, leaveType, days);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee with id " + employeeId + " not found"));

        LeaveBalance leaveBalance = leaveBalanceRepository.findByEmployeeAndLeaveType(employee, leaveType)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance of type " + leaveType + " not found for employee " + employeeId));

        if (leaveBalance.getBalance() < days) {
            logger.warn("Insufficient leave balance for employee ID: {} for leave type {}. Requested: {}, Available: {}", employeeId, leaveType, days, leaveBalance.getBalance());
            throw new IllegalArgumentException("Insufficient leave balance for " + leaveType.name() + ". Available: " + leaveBalance.getBalance() + ", Requested: " + days);
        }
        logger.info("Sufficient leave balance found for employee ID: {} for leave type {}. Available: {}", employeeId, leaveType, leaveBalance.getBalance());
    }

    private LeaveBalanceResponseDto mapToResponseDto(LeaveBalance leaveBalance) {
        LeaveBalanceResponseDto dto = modelMapper.map(leaveBalance, LeaveBalanceResponseDto.class);
        if (leaveBalance.getEmployee() != null) {
            dto.setEmployeeId(leaveBalance.getEmployee().getId());
            dto.setEmployeeName(leaveBalance.getEmployee().getEmployeeName());
        }
        return dto;
    }
}