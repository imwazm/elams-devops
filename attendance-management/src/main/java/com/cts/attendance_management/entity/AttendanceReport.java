package com.cts.attendance_management.entity;

import com.cts.attendance_management.entity.enums.AttendanceReportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalPresent;
    private int totalAbsent;
    @Enumerated(EnumType.STRING)
    private AttendanceReportType type;

    @ManyToOne
    @JoinColumn
    private Employee employee;
}
