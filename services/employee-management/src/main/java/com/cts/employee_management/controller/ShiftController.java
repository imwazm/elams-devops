package com.cts.employee_management.controller;

import com.cts.employee_management.dto.ShiftRequestDto;
import com.cts.employee_management.dto.ShiftResponseDto;
import com.cts.employee_management.entity.enums.ShiftType;
import com.cts.employee_management.service.ShiftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/shifts")
public class ShiftController {

    @Autowired
    private ShiftService shiftService;

    @PutMapping("{id}")
    public ShiftResponseDto updateShift(@PathVariable Long id, @RequestBody ShiftRequestDto shiftRequestDto) {
        return shiftService.updateShift(id, shiftRequestDto);
    }

    @GetMapping("all")
    public List<ShiftResponseDto> getAllShifts() {
        return shiftService.getAllShifts();
    }

    @GetMapping("{id}")
    public ShiftResponseDto getShiftById(@PathVariable Long id) {
        return shiftService.getShiftById(id);
    }

    @GetMapping
    public ShiftResponseDto getShiftByType(@RequestParam ShiftType type){
        return shiftService.getShiftByType(type);
    }
}