package com.cts.leave_management.service;

import com.cts.leave_management.dto.AttendanceClockInRequestDto;
import com.cts.leave_management.dto.AttendanceClockOutRequestDto;
import com.cts.leave_management.dto.AttendanceResponseDto;

import java.util.List;

public interface AttendanceService {
    AttendanceResponseDto clockIn(AttendanceClockInRequestDto attendanceClockInRequestDto);

    AttendanceResponseDto clockOut(AttendanceClockOutRequestDto attendanceClockOutRequestDto);

    void deleteAttendance(Long id);

    AttendanceResponseDto findAttendanceById(Long id);

    List<AttendanceResponseDto> findAllAttendance();
}
