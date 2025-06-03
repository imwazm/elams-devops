package com.cts.employee_management.service;

import com.cts.employee_management.dto.ShiftRequestDto;
import com.cts.employee_management.dto.ShiftResponseDto;

import java.util.List;

public interface ShiftService {

    ShiftResponseDto updateShift(Long id, ShiftRequestDto shiftRequestDto);

    List<ShiftResponseDto> getAllShifts();

    ShiftResponseDto getShiftById(Long id);
}