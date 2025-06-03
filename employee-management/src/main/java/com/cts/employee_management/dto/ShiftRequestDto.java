package com.cts.employee_management.dto;

import com.cts.employee_management.entity.enums.ShiftType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftRequestDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private ShiftType type;
}