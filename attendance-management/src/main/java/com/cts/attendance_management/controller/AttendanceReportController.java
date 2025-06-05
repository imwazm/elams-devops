package com.cts.attendance_management.controller;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.service.AttendanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("api/attendance-reports")
public class AttendanceReportController {

    @Autowired
    private AttendanceReportService attendanceReportService;

    @GetMapping
    public ResponseEntity<List<AttendanceReportDto>> getAllReports() {
        return ResponseEntity.ok(attendanceReportService.getAllReports());
    }

    @GetMapping("employee/{employeeId}")
    public ResponseEntity<List<AttendanceReportDto>> getReportsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceReportService.getReportsByEmployee(employeeId));
    }

    @GetMapping("employee/{employeeId}/weekly")
    public ResponseEntity<List<AttendanceReportDto>> getWeeklyReportsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceReportService.getReportsByEmployeeAndType(employeeId, "WEEKLY"));
    }

    @GetMapping("employee/{employeeId}/monthly")
    public ResponseEntity<List<AttendanceReportDto>> getMonthlyReportsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceReportService.getReportsByEmployeeAndType(employeeId, "MONTHLY"));
    }

    @GetMapping("employee/{employeeId}/yearly")
    public ResponseEntity<List<AttendanceReportDto>> getYearlyReportsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceReportService.getReportsByEmployeeAndType(employeeId, "YEARLY"));
    }

    @GetMapping("employee/{employeeId}/custom")
    public ResponseEntity<AttendanceReportDto> getCustomReportByEmployee(
            @PathVariable Long employeeId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return ResponseEntity.ok(attendanceReportService.getCustomReportByEmployee(employeeId, startDate, endDate));
    }
}