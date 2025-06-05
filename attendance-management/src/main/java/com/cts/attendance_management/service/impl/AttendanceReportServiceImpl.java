package com.cts.attendance_management.service.impl;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.entity.Attendance;
import com.cts.attendance_management.entity.AttendanceReport;
import com.cts.attendance_management.entity.Employee;
import com.cts.attendance_management.entity.enums.AttendanceReportType;
import com.cts.attendance_management.entity.enums.AttendanceStatus;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.repository.AttendanceReportRepository;
import com.cts.attendance_management.repository.EmployeeRepository;
import com.cts.attendance_management.service.AttendanceReportService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {

    @Autowired
    private AttendanceReportRepository attendanceReportRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void updateAttendanceReport(Attendance attendance) {
        if (attendance.getStatus() != AttendanceStatus.PRESENT) {
            return; // Only consider "PRESENT" status for reports
        }

        Employee employee = attendance.getEmployee();
        LocalDate date = attendance.getDate();

        updateReport(employee, date, AttendanceReportType.WEEKLY);
        updateReport(employee, date, AttendanceReportType.MONTHLY);
        updateReport(employee, date, AttendanceReportType.YEARLY);
    }

    private void updateReport(Employee employee, LocalDate date, AttendanceReportType type) {
        LocalDate startDate = getStartDateForType(date, type);
        LocalDate endDate = getEndDateForType(date, type);

        AttendanceReport report = attendanceReportRepository.findByEmployeeAndTypeAndStartDateAndEndDate(
                employee, type, startDate, endDate).orElseGet(() -> {
            AttendanceReport newReport = new AttendanceReport();
            newReport.setEmployee(employee);
            newReport.setType(type);
            newReport.setStartDate(startDate);
            newReport.setEndDate(endDate);
            newReport.setTotalPresent(0);
            newReport.setTotalAbsent(0);
            return newReport;
        });

        report.setTotalPresent(report.getTotalPresent() + 1);
        attendanceReportRepository.save(report);
    }

    private LocalDate getStartDateForType(LocalDate date, AttendanceReportType type) {
        if (type == AttendanceReportType.WEEKLY) {
            return date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1);
        } else if (type == AttendanceReportType.MONTHLY) {
            return date.withDayOfMonth(1);
        } else if (type == AttendanceReportType.YEARLY) {
            return date.withDayOfYear(1);
        }
        return date;
    }

    private LocalDate getEndDateForType(LocalDate date, AttendanceReportType type) {
        if (type == AttendanceReportType.WEEKLY) {
            return date.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7);
        } else if (type == AttendanceReportType.MONTHLY) {
            return date.withDayOfMonth(date.lengthOfMonth());
        } else if (type == AttendanceReportType.YEARLY) {
            return date.withDayOfYear(date.lengthOfYear());
        }
        return date;
    }

    @Override
    public List<AttendanceReportDto> getAllReports() {
        return attendanceReportRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceReportDto> getReportsByEmployee(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        return attendanceReportRepository.findByEmployee(employee).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceReportDto> getReportsByEmployeeAndType(Long employeeId, String type) {
        Employee employee = findEmployeeById(employeeId);
        AttendanceReportType reportType = AttendanceReportType.valueOf(type.toUpperCase());
        return attendanceReportRepository.findByEmployeeAndType(employee, reportType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public AttendanceReportDto getCustomReportByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = findEmployeeById(employeeId);
        List<AttendanceReport> reports = attendanceReportRepository.findByEmployeeAndStartDateBetween(employee, startDate, endDate);

        int totalPresent = reports.stream().mapToInt(AttendanceReport::getTotalPresent).sum();
        int totalAbsent = reports.stream().mapToInt(AttendanceReport::getTotalAbsent).sum();

        AttendanceReportDto customReport = new AttendanceReportDto();
        customReport.setStartDate(startDate);
        customReport.setEndDate(endDate);
        customReport.setTotalPresent(totalPresent);
        customReport.setTotalAbsent(totalAbsent);
        customReport.setType(null);
        return customReport;
    }

    private Employee findEmployeeById(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + employeeId));
    }

    private AttendanceReportDto convertToDto(AttendanceReport report) {
        AttendanceReportDto dto = modelMapper.map(report, AttendanceReportDto.class);
        dto.setEmployeeId(report.getEmployee().getId());
        return dto;
    }
}