package com.cts.employee_management.service.impl;

import com.cts.employee_management.dto.ShiftRequestDto;
import com.cts.employee_management.dto.ShiftResponseDto;
import com.cts.employee_management.entity.Shift;
import com.cts.employee_management.exception.ResourceNotFoundException;
import com.cts.employee_management.repository.ShiftRepository;
import com.cts.employee_management.service.ShiftService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShiftServiceImpl implements ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ShiftResponseDto updateShift(Long id, ShiftRequestDto shiftRequestDto) {
        Shift existingShift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift with id " + id + " not found"));

        existingShift.setStartTime(shiftRequestDto.getStartTime());
        existingShift.setEndTime(shiftRequestDto.getEndTime());
        existingShift.setType(shiftRequestDto.getType());

        Shift updatedShift = shiftRepository.save(existingShift);
        return modelMapper.map(updatedShift, ShiftResponseDto.class);
    }

    @Override
    public List<ShiftResponseDto> getAllShifts() {
        return shiftRepository.findAll().stream()
                .map(shift -> modelMapper.map(shift, ShiftResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public ShiftResponseDto getShiftById(Long id) {
        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift with id " + id + " not found"));
        return modelMapper.map(shift, ShiftResponseDto.class);
    }
}