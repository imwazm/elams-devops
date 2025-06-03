package com.cts.attendance_management.repository;

import com.cts.attendance_management.entity.AttendanceReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceReportRepository extends JpaRepository<AttendanceReport, Long> {
}