package com.cts.leave_management.entity;

import com.cts.leave_management.entity.enums.LeaveType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    private int balance;

    @ManyToOne
    @JoinColumn
    private Employee employee;
}
