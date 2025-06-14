package com.cts.attendance_management.service.impl;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.entity.Attendance;
import com.cts.attendance_management.entity.enums.AttendanceReportType;
import com.cts.attendance_management.entity.enums.AttendanceStatus;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.repository.AttendanceRepository;
import com.cts.attendance_management.service.AttendanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function; // <-- Added this import
import java.util.stream.Collectors;

@Service
public class AttendanceReportServiceImpl implements AttendanceReportService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Override
    public void updateAttendanceReport(Attendance attendance) {
        // Reports are generated on-demand. This method might be for a future feature or not directly used in reports.
    }

    @Override
    public List<AttendanceReportDto> getAllReports() {
        List<AttendanceReportDto> reports = new ArrayList<>();
        List<Employee> employees = employeeRepository.findAll();

        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        List<Attendance> allAttendanceForYear = attendanceRepository.findByDateBetween(startOfYear, endOfYear);

        Map<Long, List<Attendance>> attendanceByEmployee = allAttendanceForYear.stream()
                .collect(Collectors.groupingBy(att -> att.getEmployee().getId()));

        AtomicLong currentReportId = new AtomicLong(1); // Use AtomicLong for sequential ID across all reports
        for (Employee employee : employees) {
            List<Attendance> employeeAttendances = attendanceByEmployee.getOrDefault(employee.getId(), new ArrayList<>());
            // Pass the same AtomicLong to generateReportsForEmployee so IDs continue sequentially
            reports.addAll(generateReportsForEmployee(employee.getId(), employee.getEmployeeName(), employeeAttendances, currentReportId));
        }
        return reports;
    }

    @Override
    public List<AttendanceReportDto> getReportsByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        // For a single employee's report, fetch all attendance for the year first
        LocalDate today = LocalDate.now();
        LocalDate startOfYear = today.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = today.with(TemporalAdjusters.lastDayOfYear());
        List<Attendance> employeeAttendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startOfYear, endOfYear);

        // Reset ID counter for this specific employee's report batch, starting from 1
        return generateReportsForEmployee(employeeId, employee.getEmployeeName(), employeeAttendances, new AtomicLong(1));
    }

    @Override
    public List<AttendanceReportDto> getReportsByEmployeeAndType(Long employeeId, String type) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        AttendanceReportType reportType;
        try {
            reportType = AttendanceReportType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid report type: " + type);
        }

        List<AttendanceReportDto> reports = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Reset ID counter for this specific type of report, starting from 1
        AtomicLong currentReportId = new AtomicLong(1);

        switch (reportType) {
            case WEEKLY:
                reports.add(generateWeeklyReport(employeeId, employee.getEmployeeName(), today, currentReportId));
                break;
            case MONTHLY:
                reports.add(generateMonthlyReport(employeeId, employee.getEmployeeName(), today, currentReportId));
                break;
            case YEARLY:
                reports.add(generateYearlyReport(employeeId, employee.getEmployeeName(), today, currentReportId));
                break;
        }
        return reports;
    }

    @Override
    public AttendanceReportDto getCustomReportByEmployee(Long employeeId, LocalDate startDate, LocalDate endDate) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + employeeId));

        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startDate, endDate);

        // Reset ID counter for this single custom report, starting from 1
        AtomicLong currentReportId = new AtomicLong(1);

        // Assuming 'CUSTOM' is added to AttendanceReportType enum if needed.
        // For now, setting it to a default or null if not directly in enum and not critical for DTO.
        // If your DTO requires it to be non-null and CUSTOM is not in enum, you might need to adjust.
        return calculateReport(attendances, employeeId, employee.getEmployeeName(), startDate, endDate, AttendanceReportType.CUSTOM, currentReportId);
    }

    private List<AttendanceReportDto> generateReportsForEmployee(Long employeeId, String employeeName, List<Attendance> allEmployeeAttendances, AtomicLong currentReportId) {
        List<AttendanceReportDto> reports = new ArrayList<>();
        LocalDate today = LocalDate.now();

        // Pass the same AtomicLong to each report generation method
        reports.add(generateWeeklyReport(employeeId, employeeName, today, currentReportId));
        reports.add(generateMonthlyReport(employeeId, employeeName, today, currentReportId));
        reports.add(generateYearlyReport(employeeId, employeeName, today, currentReportId));

        return reports;
    }

    private AttendanceReportDto generateWeeklyReport(Long employeeId, String employeeName, LocalDate date, AtomicLong currentReportId) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = date.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startOfWeek, endOfWeek);
        return calculateReport(attendances, employeeId, employeeName, startOfWeek, endOfWeek, AttendanceReportType.WEEKLY, currentReportId);
    }

    private AttendanceReportDto generateMonthlyReport(Long employeeId, String employeeName, LocalDate date, AtomicLong currentReportId) {
        LocalDate startOfMonth = date.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endOfMonth = date.with(TemporalAdjusters.lastDayOfMonth());
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startOfMonth, endOfMonth);
        return calculateReport(attendances, employeeId, employeeName, startOfMonth, endOfMonth, AttendanceReportType.MONTHLY, currentReportId);
    }

    private AttendanceReportDto generateYearlyReport(Long employeeId, String employeeName, LocalDate date, AtomicLong currentReportId) {
        LocalDate startOfYear = date.with(TemporalAdjusters.firstDayOfYear());
        LocalDate endOfYear = date.with(TemporalAdjusters.lastDayOfYear());
        List<Attendance> attendances = attendanceRepository.findByEmployeeIdAndDateBetween(employeeId, startOfYear, endOfYear);
        return calculateReport(attendances, employeeId, employeeName, startOfYear, endOfYear, AttendanceReportType.YEARLY, currentReportId);
    }

    private AttendanceReportDto calculateReport(List<Attendance> attendances, Long employeeId, String employeeName,
                                                LocalDate startDate, LocalDate endDate, AttendanceReportType type, AtomicLong currentReportId) {

        int totalWorkingDays = 0;
        int totalPresentDays = 0;
        double totalWorkHours = 0.0; // Still calculated internally, though not explicitly in DTO constructor/setter for reportDto

        // Create a map for efficient lookup of attendance records by date
        Map<LocalDate, Attendance> attendanceMap = attendances.stream()
                .collect(Collectors.toMap(Attendance::getDate, Function.identity()));

        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (!isWeekend(currentDate)) { // Only consider weekdays
                totalWorkingDays++; // Increment total working days

                Attendance attendance = attendanceMap.get(currentDate);
                if (attendance != null) {
                    if (attendance.getStatus() == AttendanceStatus.PRESENT) {
                        totalPresentDays++; // Count as a present day
                        totalWorkHours += attendance.getWorkHours(); // Add work hours
                    } else if (attendance.getStatus() == AttendanceStatus.HALF_DAY) {
                        // HALF_DAY contributes to work hours, but is not counted as a full 'present day'
                        totalWorkHours += attendance.getWorkHours();
                    }
                    // ABSENT, ABNORMAL, or PENDING without clock-out will implicitly be absent for day count
                }
                // If no attendance record exists for a weekday, it's implicitly absent
            }
            currentDate = currentDate.plusDays(1);
        }

        // Calculate total absent days based on the new rule: total working days - total present days
        int totalAbsentDays = totalWorkingDays - totalPresentDays;
        // This implies that HALF_DAY, ABSENT, ABNORMAL statuses, and unrecorded working days
        // within the range will all contribute to the 'totalAbsentDays' count.

        AttendanceReportDto reportDto = new AttendanceReportDto();

        // Assign the sequential ID (1, 2, 3...)
        reportDto.setId(currentReportId.getAndIncrement());

        reportDto.setEmployeeId(employeeId);
        // Assuming employeeName is not directly set on AttendanceReportDto based on your DTO.
        // If it should be, add reportDto.setEmployeeName(employeeName);

        reportDto.setStartDate(startDate);
        reportDto.setEndDate(endDate);
        reportDto.setTotalPresent(totalPresentDays);
        reportDto.setTotalAbsent(totalAbsentDays);
        reportDto.setTotalWorkingDays(totalWorkingDays); // <-- THIS WAS THE MISSING LINE!

        reportDto.setType(type);
        return reportDto;
    }

    /**
     * Helper method to check if a given date is a weekend (Saturday or Sunday).
     * @param date The LocalDate to check.
     * @return true if the date is a Saturday or Sunday, false otherwise.
     */
    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}