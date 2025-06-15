package com.cts.api_gateway.dto;

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