package com.cts.employee_management.dto;

import com.cts.employee_management.entity.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeAuthDto {
    private Long id;
    String employeeName;
    String email;
    String role;
}