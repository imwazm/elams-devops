package com.cts.attendance_management.service;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.entity.Attendance;
import com.cts.attendance_management.entity.enums.AttendanceReportType;
import com.cts.attendance_management.entity.enums.AttendanceStatus;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.repository.AttendanceRepository;
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

    @Autowired
    private AttendanceReportService attendanceReportService;

    private LocalDate today;
    private LocalDate startOfWeek;
    private LocalDate endOfWeek;
    private LocalDate startOfMonth;
    private LocalDate endOfMonth;
    private LocalDate startOfYear;
    private LocalDate endOfYear;

    private Long employeeId;

    @BeforeEach
    void setup() {
        employeeId = 1L;
        // Setting a fixed 'today' for consistent test results.
        // This 'today' should align with the 'LocalDate.now()' behavior in your service for tests.
        today = LocalDate.of(2025, 6, 10); // Tuesday, June 10, 2025

        // Define report date ranges based on the fixed 'today'
        startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // 2025-06-09
        endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));     // 2025-06-15

        startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2025-06-01
        endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());   // 2025-06-30

        startOfYear = today.with(TemporalAdjusters.firstDayOfYear());  // 2025-01-01
        endOfYear = today.with(TemporalAdjusters.lastDayOfYear());     // 2025-12-31

    }

    // --- Helper to create attendance records ---
    private Attendance createAttendance(Long id, LocalDate date, LocalTime clockIn, LocalTime clockOut, double workHours, AttendanceStatus status, Long empId) {
        return new Attendance(id, clockIn, clockOut, workHours, date, status, empId);
    }


    @Test
    void getReportsByEmployee_shouldReturnReportsForSpecificEmployee() {
        // Attendance data specifically for the employee within the tested range
        List<Attendance> employeeAttendance = Arrays.asList(
                createAttendance(1L, startOfWeek.plusDays(0), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employeeId), // Monday
                createAttendance(2L, startOfWeek.plusDays(1), null, null, 0.0, AttendanceStatus.ABSENT, employeeId), // Tuesday
                createAttendance(3L, startOfWeek.plusDays(2), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employeeId) // Wednesday
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
                createAttendance(1L, LocalDate.of(2025, 6, 9), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employeeId)
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
                createAttendance(1L, LocalDate.of(2025, 5, 1), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employeeId), // Thu
                createAttendance(2L, LocalDate.of(2025, 5, 2), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employeeId)  // Fri
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