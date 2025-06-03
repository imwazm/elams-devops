package com.cts.employee_management.service.impl;

import com.cts.employee_management.dto.AttendanceClockInRequestDto;
import com.cts.employee_management.dto.AttendanceClockOutRequestDto;
import com.cts.employee_management.dto.AttendanceResponseDto;
import com.cts.employee_management.entity.Attendance;
import com.cts.employee_management.entity.Employee;
import com.cts.employee_management.entity.enums.AttendanceStatus;
import com.cts.employee_management.exception.AttendanceRegisterException;
import com.cts.employee_management.exception.ResourceNotFoundException;
import com.cts.employee_management.repository.AttendanceRepository;
import com.cts.employee_management.repository.EmployeeRepository;
import com.cts.employee_management.service.AttendanceService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.Temporal;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceServiceImpl.class);

    @Autowired
    AttendanceRepository attendanceRepository;

    @Autowired
    ModelMapper modelMapper;
    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public AttendanceResponseDto clockIn(AttendanceClockInRequestDto attendanceClockInRequestDto) {
        Optional<Attendance> checkAttendance = attendanceRepository.findByEmployeeIdAndDate(
                attendanceClockInRequestDto.getEmployeeId(),
                attendanceClockInRequestDto.getDate());
        if(checkAttendance.isPresent()){
            String msg = "Employee with id "+ checkAttendance.get().getId()
                    + " has already clocked in at "+ attendanceClockInRequestDto.getClockInTime();
            logger.error(msg);
            throw new AttendanceRegisterException(msg);
        }
        Attendance attendance = modelMapper.map(attendanceClockInRequestDto, Attendance.class);
        Employee employee = findEmployeeByIdHelper(attendanceClockInRequestDto.getEmployeeId());
        attendance.setEmployee(employee);
        Attendance savedAttendance = attendanceRepository.save(attendance);
        String msg = "Employee with id "+ attendanceClockInRequestDto.getEmployeeId()
                +" has clocked in at " + attendanceClockInRequestDto.getClockInTime();
        logger.info(msg);
        AttendanceResponseDto mappedDto =  modelMapper.map(savedAttendance, AttendanceResponseDto.class);
        mappedDto.setEmployeeId(attendanceClockInRequestDto.getEmployeeId());
        return mappedDto;
    }

    @Override
    public AttendanceResponseDto clockOut(AttendanceClockOutRequestDto attendanceClockOutRequestDto) {
        logger.debug("Fetching employee from the database.");
        Optional<Attendance> checkAttendance = attendanceRepository.findByEmployeeIdAndDate(
                attendanceClockOutRequestDto.getEmployeeId(),
                attendanceClockOutRequestDto.getDate());

        if(checkAttendance.isEmpty()){
            String msg = "Employee with id "+ attendanceClockOutRequestDto.getEmployeeId()
                    + " has to be clocked in inorder to clock out";
            logger.error(msg);
            throw new AttendanceRegisterException(msg);
        }

        if(checkAttendance.get().getClockOutTime()!=null){
            String msg = "Employee with id "+ checkAttendance.get().getId()
                    + " has already clocked out at "+ checkAttendance.get().getClockOutTime();
            logger.error(msg);
            throw new AttendanceRegisterException(msg);
        }
        Attendance attendance = checkAttendance.get();
        attendance.setClockOutTime(attendanceClockOutRequestDto.getClockOutTime());
        attendance.setWorkHours(calculateWorkHours(attendance.getClockInTime(),
                attendanceClockOutRequestDto.getClockOutTime()));
        attendance.setStatus(determineAttendanceStatus(attendance.getWorkHours()));
        Attendance savedAttendance = attendanceRepository.save(attendance);
        String msg = "Employee with id "+ attendanceClockOutRequestDto.getEmployeeId()
                +" has clocked out at " + attendanceClockOutRequestDto.getClockOutTime();
        logger.info(msg);
        AttendanceResponseDto mappedDto =  modelMapper.map(savedAttendance, AttendanceResponseDto.class);
        mappedDto.setEmployeeId(attendanceClockOutRequestDto.getEmployeeId());
        return mappedDto;
    }

    @Override
    public void deleteAttendance(Long id) {
        attendanceRepository.deleteById(id);
    }

    @Override
    public AttendanceResponseDto findAttendanceById(Long id) {
        Optional<Attendance> savedAttendance = attendanceRepository.findById(id);
        if(savedAttendance.isEmpty()){
            String msg = "Attendance with Id "+id+" not found.";
            logger.error(msg);
            throw new ResourceNotFoundException(msg);
        }
        logger.info("Attendance with id "+id+" Found");
        return modelMapper.map(savedAttendance.get(), AttendanceResponseDto.class);
    }

    @Override
    public List<AttendanceResponseDto> findAllAttendance() {
        logger.info("Fetching all attendances");
        return attendanceRepository.findAll()
                .stream().map((a) -> {
                    AttendanceResponseDto mapped = modelMapper.map(a, AttendanceResponseDto.class);
                    mapped.setEmployeeId(a.getEmployee().getId());
                    return mapped;
                }).toList();
    }

    private double calculateWorkHours(Temporal clockInTime, Temporal clockOutTime){
        logger.debug("Calculating working hours of "+clockInTime+" and "+clockOutTime);
        Duration duration = Duration.between(clockInTime, clockOutTime);
        double workHours =  (double)duration.toSeconds()/3600;
        return workHours;
    }

    private AttendanceStatus determineAttendanceStatus(double workHours) {

        if (workHours >= 7.5) {
            return AttendanceStatus.PRESENT;
        } else if (workHours >= 3.5) {
            return AttendanceStatus.HALF_DAY;
        } else if (workHours > 0) {
            return AttendanceStatus.ABNORMAL;
        } else {
            return AttendanceStatus.ABNORMAL;
        }
    }

    private Employee findEmployeeByIdHelper(Long id){
        logger.info("Searching for employee with ID: " + id);
        return employeeRepository.findById(id).orElseThrow(
                ()->new ResourceNotFoundException("Employee with id "+id+" not found")
        );
    }
}
