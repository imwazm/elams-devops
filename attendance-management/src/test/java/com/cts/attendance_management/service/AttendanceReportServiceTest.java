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
        today = LocalDate.of(2025, 6, 10); // Tuesday
        employee = new Employee(1L, "John Doe", "john.doe@example.com", Role.EMPLOYEE, null, null, null);

        // Define report date ranges
        startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)); // 2025-06-09
        endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));     // 2025-06-15

        startOfMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2025-06-01
        endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());   // 2025-06-30

        startOfYear = today.with(TemporalAdjusters.firstDayOfYear());  // 2025-01-01
        endOfYear = today.with(TemporalAdjusters.lastDayOfYear());     // 2025-12-31

        // Mock employee existence
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
    }

    // --- Helper to create attendance records ---
    private Attendance createAttendance(Long id, LocalDate date, LocalTime clockIn, LocalTime clockOut, double workHours, AttendanceStatus status, Employee emp) {
        return new Attendance(id, clockIn, clockOut, workHours, date, status, emp);
    }

    @Test
    void getAllReports_shouldReturnAllReportsForEmployee() {
        // Mock attendance data for the year for John Doe
        List<Attendance> allYearlyAttendance = Arrays.asList(
                createAttendance(100L, LocalDate.of(2025, 6, 9), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Mon
                createAttendance(101L, LocalDate.of(2025, 6, 10), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employee), // Tue (Half Day)
                createAttendance(102L, LocalDate.of(2025, 6, 11), null, null, 0.0, AttendanceStatus.ABSENT, employee), // Wed (Absent)
                createAttendance(103L, LocalDate.of(2025, 6, 13), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Fri

                // Some past month data for monthly/yearly
                createAttendance(104L, LocalDate.of(2025, 5, 1), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee),
                createAttendance(105L, LocalDate.of(2025, 5, 2), null, null, 0.0, AttendanceStatus.ABSENT, employee)
        );
        when(attendanceRepository.findByDateBetween(any(LocalDate.class), any(LocalDate.class))).thenReturn(allYearlyAttendance);

        List<AttendanceReportDto> reports = attendanceReportService.getAllReports();

        assertThat(reports, notNullValue());
        assertThat(reports.size(), is(3)); // Weekly, Monthly, Yearly

        // Verify weekly report
        AttendanceReportDto weeklyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.WEEKLY)
                .findFirst().orElseThrow();
        assertThat(weeklyReport.getEmployeeId(), is(employee.getId()));
        assertThat(weeklyReport.getStartDate(), is(startOfWeek));
        assertThat(weeklyReport.getEndDate(), is(endOfWeek));
        assertThat(weeklyReport.getTotalWorkingDays(), is(5)); // Mon, Tue, Wed, Thu, Fri in this week
        assertThat(weeklyReport.getTotalPresent(), is(2)); // Mon, Fri (only PRESENT)
        assertThat(weeklyReport.getTotalAbsent(), is(3)); // (Tue (HALF_DAY), Wed (ABSENT), Thu (No Record) )


        // Verify monthly report (current month: June)
        AttendanceReportDto monthlyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.MONTHLY)
                .findFirst().orElseThrow();
        // Assuming June 2025 has 21 working days (5 weekdays x 4 weeks + 1 extra weekday)
        assertThat(monthlyReport.getTotalWorkingDays(), is(21));
        assertThat(monthlyReport.getTotalPresent(), is(2)); // Only Present days in June from mocked data
        assertThat(monthlyReport.getTotalAbsent(), is(19)); // 21 working days - 2 present days = 19


        // Verify yearly report
        AttendanceReportDto yearlyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.YEARLY)
                .findFirst().orElseThrow();
        // 2025 has 260 working days (52 * 5)
        assertThat(yearlyReport.getTotalWorkingDays(), is(260));
        assertThat(yearlyReport.getTotalPresent(), is(3)); // 2 from June + 1 from May = 3
        assertThat(yearlyReport.getTotalAbsent(), is(257)); // 260 - 3 = 257

    }

    @Test
    void getReportsByEmployee_shouldReturnReportsForSpecificEmployee() {
        List<Attendance> employeeAttendance = Arrays.asList(
                createAttendance(1L, startOfWeek.plusDays(0), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Monday
                createAttendance(2L, startOfWeek.plusDays(1), null, null, 0.0, AttendanceStatus.ABSENT, employee), // Tuesday
                createAttendance(3L, startOfWeek.plusDays(2), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employee) // Wednesday
        );
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class))).thenReturn(employeeAttendance);

        List<AttendanceReportDto> reports = attendanceReportService.getReportsByEmployee(1L);

        assertThat(reports, notNullValue());
        assertThat(reports.size(), is(3)); // Weekly, Monthly, Yearly for the employee

        // Verify one of the reports (e.g., weekly)
        AttendanceReportDto weeklyReport = reports.stream()
                .filter(r -> r.getType() == AttendanceReportType.WEEKLY)
                .findFirst().orElseThrow();
        assertThat(weeklyReport.getEmployeeId(), is(1L));
        assertThat(weeklyReport.getStartDate(), is(startOfWeek));
        assertThat(weeklyReport.getEndDate(), is(endOfWeek));
        assertThat(weeklyReport.getTotalWorkingDays(), is(5)); // Mon-Fri
        assertThat(weeklyReport.getTotalPresent(), is(1)); // Only Monday is PRESENT
        assertThat(weeklyReport.getTotalAbsent(), is(4)); // 5 - 1 = 4 (includes Tue, Wed, Thu, Fri as absent/not present)

    }

    @Test
    void getReportsByEmployee_shouldThrowResourceNotFoundException_whenEmployeeDoesNotExist() {
        Long nonExistentEmployeeId = 99L;
        when(employeeRepository.findById(nonExistentEmployeeId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                attendanceReportService.getReportsByEmployee(nonExistentEmployeeId)
        );
        assertThat(exception.getMessage(), is("Employee not found with id: " + nonExistentEmployeeId));
    }

    @Test
    void getReportsByEmployeeAndType_shouldReturnSpecificReportType() {
        List<Attendance> employeeAttendance = Collections.singletonList(
                createAttendance(1L, LocalDate.of(2025, 6, 9), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee)
        );
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(1L), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(employeeAttendance);

        List<AttendanceReportDto> reports = attendanceReportService.getReportsByEmployeeAndType(1L, "WEEKLY");

        assertThat(reports, notNullValue());
        assertThat(reports.size(), is(1));
        assertThat(reports.get(0).getType(), is(AttendanceReportType.WEEKLY));
        // For the weekly report, total working days for 2025-06-09 to 2025-06-15 is 5
        assertThat(reports.get(0).getTotalWorkingDays(), is(5));
        assertThat(reports.get(0).getTotalPresent(), is(1));
        assertThat(reports.get(0).getTotalAbsent(), is(4)); // 5 working days - 1 present day
    }

    @Test
    void getReportsByEmployeeAndType_shouldThrowIllegalArgumentException_whenInvalidType() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                attendanceReportService.getReportsByEmployeeAndType(1L, "INVALID_TYPE")
        );
        assertThat(exception.getMessage(), is("Invalid report type: INVALID_TYPE"));
    }

    @Test
    void getCustomReportByEmployee_shouldReturnCorrectCustomReport() {
        LocalDate customStartDate = LocalDate.of(2025, 5, 1);
        LocalDate customEndDate = LocalDate.of(2025, 5, 7); // Wednesday

        List<Attendance> customAttendance = Arrays.asList(
                createAttendance(1L, LocalDate.of(2025, 5, 1), LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, AttendanceStatus.PRESENT, employee), // Thu
                createAttendance(2L, LocalDate.of(2025, 5, 2), LocalTime.of(9, 0), LocalTime.of(13, 0), 4.0, AttendanceStatus.HALF_DAY, employee)  // Fri
        );
        when(attendanceRepository.findByEmployeeIdAndDateBetween(eq(1L), eq(customStartDate), eq(customEndDate)))
                .thenReturn(customAttendance);

        AttendanceReportDto report = attendanceReportService.getCustomReportByEmployee(1L, customStartDate, customEndDate);

        assertThat(report, notNullValue());
        assertThat(report.getEmployeeId(), is(1L));
        assertThat(report.getStartDate(), is(customStartDate));
        assertThat(report.getEndDate(), is(customEndDate));
        // Total working days from 2025-05-01 (Thu) to 2025-05-07 (Wed): Thu, Fri, Mon, Tue, Wed = 5 days
        assertThat(report.getTotalWorkingDays(), is(5));
        assertThat(report.getTotalPresent(), is(1)); // Only 2025-05-01 is PRESENT
        assertThat(report.getTotalAbsent(), is(4)); // 5 working days - 1 present day = 4 (includes HALF_DAY and unrecorded days)

        assertThat(report.getType(), is(AttendanceReportType.CUSTOM)); // This will be null if CUSTOM is not in enum, adjust as needed
    }
}