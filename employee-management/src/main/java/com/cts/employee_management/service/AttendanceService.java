package com.cts.employee_management.service;

import com.cts.employee_management.dto.AttendanceClockInRequestDto;
import com.cts.employee_management.dto.AttendanceClockOutRequestDto;
import com.cts.employee_management.dto.AttendanceResponseDto;

import java.util.List;

public interface AttendanceService {
    AttendanceResponseDto clockIn(AttendanceClockInRequestDto attendanceClockInRequestDto);

    AttendanceResponseDto clockOut(AttendanceClockOutRequestDto attendanceClockOutRequestDto);

    void deleteAttendance(Long id);

    AttendanceResponseDto findAttendanceById(Long id);

    List<AttendanceResponseDto> findAllAttendance();
}
