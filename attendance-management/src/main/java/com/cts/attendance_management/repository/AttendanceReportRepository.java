package com.cts.attendance_management.repository;

import com.cts.attendance_management.entity.AttendanceReport;
import com.cts.attendance_management.entity.Employee;
import com.cts.attendance_management.entity.enums.AttendanceReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long> {
    Optional<AttendanceReport> findByEmployeeAndTypeAndStartDateAndEndDate(Employee employee, AttendanceReportType type, LocalDate startDate, LocalDate endDate);

    List<AttendanceReport> findByEmployee(Employee employee);

    List<AttendanceReport> findByEmployeeAndType(Employee employee, AttendanceReportType reportType);

    List<AttendanceReport> findByEmployeeAndStartDateBetween(Employee employee, LocalDate startDate, LocalDate endDate);
}