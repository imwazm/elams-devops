package com.cts.attendance_management.service;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.entity.Attendance;
import com.cts.attendance_management.entity.Employee;
import com.cts.attendance_management.entity.enums.AttendanceReportType;
import com.cts.attendance_management.entity.enums.AttendanceStatus;
import com.cts.attendance_management.entity.enums.Role;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.repository.AttendanceRepository;
import com.cts.attendance_management.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors; // Required for .collect(Collectors.toList())

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
public class AttendanceReportServiceTest {

    @MockitoBean
    private AttendanceRepository attendanceRepository;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private AttendanceReportService attendanceReportService;

    private Employee employee;
    private LocalDate today;
    private LocalDate startOfWeek;
    private LocalDate endOfWeek;
    private LocalDate startOfMonth;
    private LocalDate endOfMonth;
    private LocalDate startOfYear;
    private LocalDate endOfYear;

    @BeforeEach
    void setup() {
        // Setting a fixed 'today' for consistent test results.
        // This 'today' should align with the 'LocalDate.now()' behavior in your service for tests.
        today = LocalDate.of(2025, 6, 10); // Tuesday, June 10, 2025

        // Initialize employee data
        employee = new Employee(1L, "John Doe", "john.doe@example.com", Role.EMPLOYEE, null, null, null);

        // Define report date ranges based on the fixed 'today'
        startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // 2025-06-09
        endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));     // 2025-06-15

        startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2025-06-01
        endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());   // 2025-06-30

        startOfYear = today.with(TemporalAdjusters.firstDayOfYear());  // 2025-01-01
        endOfYear = today.with(TemporalAdjusters.lastDayOfYear());     // 2025-12-31

        // Mock common repository calls used across tests
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
    }

    // --- Helper to create attendance records ---
    private Attendance createAttendance(Long id, LocalDate date, LocalTime clockIn, LocalTime clockOut, double workHours, AttendanceStatus status, Employee emp) {
        return new Attendance(id, clockIn, clockOut, workHours, date, status, emp);
    }

    @Test
    void getAllReports_shouldReturnAllReportsForEmployee() {
        // Mock attendance data for the entire year for the employee
        List<Attendance> allYearlyAttendance = Arrays.asList(
                // Current week (June 9-15) - within current month/year
                createAttendance(100L, LocalDate.of(2025, 6, 9), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Mon
                createAttendance(101L, LocalDate.of(2025, 6, 10), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employee), // Tue (Half Day)
                createAttendance(102L, LocalDate.of(2025, 6, 11), null, null, 0.0, AttendanceStatus.ABSENT, employee), // Wed (Absent)
                createAttendance(103L, LocalDate.of(2025, 6, 13), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Fri

                // Past month data (May) - for monthly/yearly calculations
                createAttendance(104L, LocalDate.of(2025, 5, 1), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee),
                createAttendance(105L, LocalDate.of(2025, 5, 2), null, null, 0.0, AttendanceStatus.ABSENT, employee)
        );

        // 1. Mock for the initial `findByDateBetween` call in `getAllReports()`
        // This is used to fetch all attendance for the year which the service then groups by employee.
        when(attendanceRepository.findByDateBetween(eq(startOfYear), eq(endOfYear))).thenReturn(allYearlyAttendance);

        // 2. Mocks for the `findByEmployeeIdAndDateBetween` calls made internally by
        // `generateWeeklyReport`, `generateMonthlyReport`, and `generateYearlyReport` methods.
        // These methods fetch attendance for specific date ranges for a given employee.

        // Filter `allYearlyAttendance` to create specific lists for each report type's date range.
        List<Attendance> weeklyAttendanceForEmployee = allYearlyAttendance.stream()
                .filter(att -> !att.getDate().isBefore(startOfWeek) && !att.getDate().isAfter(endOfWeek) && att.getEmployee().getId().equals(employee.getId()))
                .collect(Collectors.toList());
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(employee.getId()), eq(startOfWeek), eq(endOfWeek)))
                .thenReturn(weeklyAttendanceForEmployee);

        List<Attendance> monthlyAttendanceForEmployee = allYearlyAttendance.stream()
                .filter(att -> !att.getDate().isBefore(startOfMonth) && !att.getDate().isAfter(endOfMonth) && att.getEmployee().getId().equals(employee.getId()))
                .collect(Collectors.toList());
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(employee.getId()), eq(startOfMonth), eq(endOfMonth)))
                .thenReturn(monthlyAttendanceForEmployee);

        List<Attendance> yearlyAttendanceForEmployee = allYearlyAttendance.stream()
                .filter(att -> !att.getDate().isBefore(startOfYear) && !att.getDate().isAfter(endOfYear) && att.getEmployee().getId().equals(employee.getId()))
                .collect(Collectors.toList());
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(employee.getId()), eq(startOfYear), eq(endOfYear)))
                .thenReturn(yearlyAttendanceForEmployee);


        // --- Act ---
        List<AttendanceReportDto> reports = attendanceReportService.getAllReports();

        // --- Assert ---
        assertThat(reports, notNullValue());
        assertThat(reports.size(), is(3)); // Expecting Weekly, Monthly, Yearly reports for the employee

        // Verify weekly report
        AttendanceReportDto weeklyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.WEEKLY)
                .findFirst().orElseThrow();
        assertThat(weeklyReport.getEmployeeId(), is(employee.getId()));
        assertThat(weeklyReport.getStartDate(), is(startOfWeek));
        assertThat(weeklyReport.getEndDate(), is(endOfWeek));
        assertThat(weeklyReport.getTotalWorkingDays(), is(5)); // Mon-Fri in this week (June 9-15, 2025)
        assertThat(weeklyReport.getTotalPresent(), is(2)); // Mon (June 9), Fri (June 13) are PRESENT
        assertThat(weeklyReport.getTotalAbsent(), is(3)); // 5 working days - 2 present days = 3 (Tue (HALF_DAY), Wed (ABSENT), Thu (No Record) )


        // Verify monthly report (current month: June 2025)
        AttendanceReportDto monthlyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.MONTHLY)
                .findFirst().orElseThrow();
        assertThat(monthlyReport.getStartDate(), is(startOfMonth));
        assertThat(monthlyReport.getEndDate(), is(endOfMonth));
        assertThat(monthlyReport.getTotalWorkingDays(), is(21)); // June 2025 has 21 weekdays
        assertThat(monthlyReport.getTotalPresent(), is(2)); // Only PRESENT days in June from mocked data (June 9, June 13)
        assertThat(monthlyReport.getTotalAbsent(), is(19)); // 21 working days - 2 present days = 19


        // Verify yearly report (2025)
        AttendanceReportDto yearlyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.YEARLY)
                .findFirst().orElseThrow();
        assertThat(yearlyReport.getStartDate(), is(startOfYear));
        assertThat(yearlyReport.getEndDate(), is(endOfYear));
        assertThat(yearlyReport.getTotalWorkingDays(), is(261)); // 2025 has 261 weekdays (52 full weeks * 5 days + 1 extra weekday (Jan 1, 2025 is a Wednesday))
        assertThat(yearlyReport.getTotalPresent(), is(3)); // 2 from June + 1 from May = 3 (2025-06-09, 2025-06-13, 2025-05-01)
        assertThat(yearlyReport.getTotalAbsent(), is(258)); // 261 - 3 = 258
    }

    @Test
    void getReportsByEmployee_shouldReturnReportsForSpecificEmployee() {
        // Attendance data specifically for the employee within the tested range
        List<Attendance> employeeAttendance = Arrays.asList(
                createAttendance(1L, startOfWeek.plusDays(0), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Monday
                createAttendance(2L, startOfWeek.plusDays(1), null, null, 0.0, AttendanceStatus.ABSENT, employee), // Tuesday
                createAttendance(3L, startOfWeek.plusDays(2), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employee) // Wednesday
        );
        // Mock ALL calls to `findByEmployeeIdAndDateBetween` for this employee and any date range.
        // This is a broad mock to satisfy all weekly/monthly/yearly internal calls within the service's method.
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(employeeAttendance);

        // --- Act ---
        List<AttendanceReportDto> reports = attendanceReportService.getReportsByEmployee(1L);

        // --- Assert ---
        assertThat(reports, notNullValue());
        assertThat(reports.size(), is(3)); // Weekly, Monthly, Yearly for the employee

        // Verify weekly report
        AttendanceReportDto weeklyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.WEEKLY)
                .findFirst().orElseThrow();
        assertThat(weeklyReport.getEmployeeId(), is(1L));
        assertThat(weeklyReport.getStartDate(), is(startOfWeek));
        assertThat(weeklyReport.getEndDate(), is(endOfWeek));
        assertThat(weeklyReport.getTotalWorkingDays(), is(5)); // Mon-Fri
        assertThat(weeklyReport.getTotalPresent(), is(1)); // Only Monday (2025-06-09) is PRESENT
        assertThat(weeklyReport.getTotalAbsent(), is(4)); // 5 - 1 = 4 (includes Tue (ABSENT), Wed (HALF_DAY), Thu, Fri (No Record) )
    }

    @Test
    void getReportsByEmployee_shouldThrowResourceNotFoundException_whenEmployeeDoesNotExist() {
        Long nonExistentEmployeeId = 99L;
        // Mock that the employee does not exist
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        // --- Act & Assert ---
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                attendanceReportService.getReportsByEmployee(nonExistentEmployeeId)
        );
        assertThat(exception.getMessage(), is("Employee not found with id: " + nonExistentEmployeeId));
    }

    @Test
    void getReportsByEmployeeAndType_shouldReturnSpecificReportType() {
        // Attendance data for the specific week, as requested by the test
        List<Attendance> employeeAttendance = Collections.singletonList(
                createAttendance(1L, LocalDate.of(2025, 6, 9), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee)
        );
        // Mock `findByEmployeeIdAndDateBetween` for any date range to return this specific data.
        // This is sufficient because this service method calculates only ONE report type.
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(employeeAttendance);

        // --- Act ---
        List<AttendanceReportDto> reports = attendanceReportService.getReportsByEmployeeAndType(1L, "WEEKLY");

        // --- Assert ---
        assertThat(reports, notNullValue());
        assertThat(reports.size(), is(1));
        assertThat(reports.get(0).getType(), is(AttendanceReportType.WEEKLY));
        assertThat(reports.get(0).getTotalWorkingDays(), is(5)); // Mon-Fri
        assertThat(reports.get(0).getTotalPresent(), is(1)); // Only June 9 is PRESENT
        assertThat(reports.get(0).getTotalAbsent(), is(4)); // 5 - 1 = 4
    }

    @Test
    void getReportsByEmployeeAndType_shouldThrowIllegalArgumentException_whenInvalidType() {
        // --- Act & Assert ---
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                attendanceReportService.getReportsByEmployeeAndType(1L, "INVALID_TYPE")
        );
        assertThat(exception.getMessage(), is("Invalid report type: INVALID_TYPE"));
    }

    @Test
    void getCustomReportByEmployee_shouldReturnCorrectCustomReport() {
        LocalDate customStartDate = LocalDate.of(2025, 5, 1); // Thursday
        LocalDate customEndDate = LocalDate.of(2025, 5, 7); // Wednesday

        // Attendance data for the custom date range
        List<Attendance> customAttendance = Arrays.asList(
                createAttendance(1L, LocalDate.of(2025, 5, 1), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Thu
                createAttendance(2L, LocalDate.of(2025, 5, 2), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employee)  // Fri
        );
        // Mock `findByEmployeeIdAndDateBetween` for the exact custom date range
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(1L), eq(customStartDate), eq(customEndDate)))
                .thenReturn(customAttendance);

        // --- Act ---
        AttendanceReportDto report = attendanceReportService.getCustomReportByEmployee(1L, customStartDate, customEndDate);

        // --- Assert ---
        assertThat(report, notNullValue());
        assertThat(report.getEmployeeId(), is(1L));
        assertThat(report.getStartDate(), is(customStartDate));
        assertThat(report.getEndDate(), is(customEndDate));
        // Total working days from 2025-05-01 (Thu) to 2025-05-07 (Wed): May 1, 2, 5, 6, 7 = 5 days
        assertThat(report.getTotalWorkingDays(), is(5));
        assertThat(report.getTotalPresent(), is(1)); // Only 2025-05-01 is PRESENT
        assertThat(report.getTotalAbsent(), is(4)); // 5 working days - 1 present day = 4 (includes HALF_DAY and unrecorded days)

        assertThat(report.getType(), is(AttendanceReportType.CUSTOM));
    }
}