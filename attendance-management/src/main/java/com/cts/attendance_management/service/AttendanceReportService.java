package com.cts.attendance_management.service;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.entity.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceReportService {
    List<AttendanceReportDto> getReportsByEmployee(Long employeeId);
    List<AttendanceReportDto> getReportsByEmployeeAndType(Long employeeId, String type);
    AttendanceReportDto getCustomReportByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate);
}