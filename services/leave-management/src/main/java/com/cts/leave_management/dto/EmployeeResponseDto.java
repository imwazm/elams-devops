package com.cts.leave_management.dto;

import com.cts.leave_management.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponseDto {
    private Long id;
    String employeeName;
    String email;
    Role role;
    private Long shiftId;
    private Long managerId;
}