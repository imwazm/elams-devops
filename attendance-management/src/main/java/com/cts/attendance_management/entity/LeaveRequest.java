package com.cts.attendance_management.entity;

import com.cts.attendance_management.entity.enums.LeaveRequestStatus;
import com.cts.attendance_management.entity.enums.LeaveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private LeaveRequestStatus status;
    private String reason;

    @ManyToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
}
