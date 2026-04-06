package com.cts.attendance_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDto {
    private Long id;
    String employeeName;
    String email;
}