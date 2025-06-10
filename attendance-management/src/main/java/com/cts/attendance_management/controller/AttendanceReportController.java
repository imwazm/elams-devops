package com.cts.attendance_management.controller;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.exception.ResourceNotFoundException; // Import for ResourceNotFoundException
import com.cts.attendance_management.service.AttendanceReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; // Import for HttpStatus
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat; // Import for @DateTimeFormat

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance-reports") // Added leading slash for clarity
public class AttendanceReportController {

    @Autowired
    private AttendanceReportService attendanceReportService;

    // --- Exception Handlers ---
    // Handles cases where an employee is not found
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND) // Returns 404 Not Found
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Handles cases of invalid input (e.g., invalid report type, invalid date range)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // Returns 400 Bad Request
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    // --- End Exception Handlers ---


    @GetMapping
    public ResponseEntity<List<AttendanceReportDto>> getAllReports() {
        return ResponseEntity.ok(attendanceReportService.getAllReports());
    }

    @GetMapping("/employee/{employeeId}") // Path for getting all reports for a specific employee
    public ResponseEntity<List<AttendanceReportDto>> getReportsByEmployee(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceReportService.getReportsByEmployee(employeeId));
    }

    // Consolidated endpoint for WEEKLY, MONTHLY, YEARLY reports
    // This matches the test paths: /api/attendance-reports/employee/{employeeId}/type/{type}
    @GetMapping("/employee/{employeeId}/type/{type}")
    public ResponseEntity<List<AttendanceReportDto>> getReportsByEmployeeAndType(
            @PathVariable Long employeeId,
            @PathVariable String type) { // 'type' will be "WEEKLY", "MONTHLY", "YEARLY", or "INVALID_TYPE"
        return ResponseEntity.ok(attendanceReportService.getReportsByEmployeeAndType(employeeId, type));
    }

    @GetMapping("/employee/{employeeId}/custom")
    public ResponseEntity<AttendanceReportDto> getCustomReportByEmployee(
            @PathVariable Long employeeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, // Added @DateTimeFormat
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) { // Added @DateTimeFormat
        return ResponseEntity.ok(attendanceReportService.getCustomReportByEmployee(employeeId, startDate, endDate));
    }
}