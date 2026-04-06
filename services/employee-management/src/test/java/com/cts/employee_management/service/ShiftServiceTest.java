package com.cts.employee_management.service;

import com.cts.employee_management.dto.ShiftRequestDto;
import com.cts.employee_management.dto.ShiftResponseDto;
import com.cts.employee_management.entity.Shift;
import com.cts.employee_management.exception.ResourceNotFoundException;
import com.cts.employee_management.repository.ShiftRepository;
import com.cts.employee_management.service.impl.ShiftServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.cts.employee_management.entity.enums.ShiftType.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ShiftServiceImpl.class, ModelMapper.class})
public class ShiftServiceTest {

    @MockitoBean
    private ShiftRepository shiftRepository;

    @Autowired
    private ShiftService shiftService;

    @Autowired
    private ModelMapper modelMapper;

    private Shift morningShift, eveningShift;
    private ShiftRequestDto morningShiftRequestDto, updatedMorningShiftRequestDto;
    private ShiftResponseDto morningShiftResponseDto, eveningShiftResponseDto;

    @BeforeEach
    void setup() {

        morningShift = new Shift(1L, LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), MORNING);
        eveningShift = new Shift(2L, LocalTime.of(16, 0, 0), LocalTime.of(0, 0, 0), EVENING);


        morningShiftRequestDto = new ShiftRequestDto(LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), MORNING);
        updatedMorningShiftRequestDto = new ShiftRequestDto(LocalTime.of(9, 0, 0), LocalTime.of(17, 0, 0), GENERAL);


        morningShiftResponseDto = new ShiftResponseDto(1L, LocalTime.of(8, 0, 0), LocalTime.of(16, 0, 0), MORNING);
        eveningShiftResponseDto = new ShiftResponseDto(2L, LocalTime.of(16, 0, 0), LocalTime.of(0, 0, 0), EVENING);
    }

    @Test
    void getAllShifts_shouldReturnListOfAllShifts() {

        when(shiftRepository.findAll()).thenReturn(Arrays.asList(morningShift, eveningShift));


        List<ShiftResponseDto> actualShifts = shiftService.getAllShifts();


        assertThat(actualShifts, hasSize(2));
        assertThat(actualShifts, containsInAnyOrder(morningShiftResponseDto, eveningShiftResponseDto));
    }

    @Test
    void getShiftById_shouldReturnCorrectShift_whenIdExists() {
        when(shiftRepository.findById(anyLong())).thenReturn(Optional.of(morningShift));


        ShiftResponseDto actualShift = shiftService.getShiftById(1L);

        assertThat(actualShift, is(morningShiftResponseDto));
    }

    @Test
    void getShiftById_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {

        when(shiftRepository.findById(anyLong())).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                shiftService.getShiftById(99L)
        );

        assertThat(exception.getMessage(), containsString("Shift with id 99 not found"));
    }

    @Test
    void updateShift_shouldUpdateAndReturnUpdatedShift_whenIdExists() {

        when(shiftRepository.findById(anyLong())).thenReturn(Optional.of(morningShift));

        when(shiftRepository.save(any(Shift.class))).thenReturn(new Shift(
                1L, updatedMorningShiftRequestDto.getStartTime(),
                updatedMorningShiftRequestDto.getEndTime(),
                updatedMorningShiftRequestDto.getType()));


        ShiftResponseDto expectedUpdatedResponse = new ShiftResponseDto(
                1L, updatedMorningShiftRequestDto.getStartTime(),
                updatedMorningShiftRequestDto.getEndTime(),
                updatedMorningShiftRequestDto.getType()
        );


        ShiftResponseDto actualUpdatedShift = shiftService.updateShift(1L, updatedMorningShiftRequestDto);


        assertThat(actualUpdatedShift, is(expectedUpdatedResponse));
    }

    @Test
    void updateShift_shouldThrowResourceNotFoundException_whenIdDoesNotExist() {

        when(shiftRepository.findById(anyLong())).thenReturn(Optional.empty());


        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () ->
                shiftService.updateShift(99L, updatedMorningShiftRequestDto)
        );


        assertThat(exception.getMessage(), containsString("Shift with id 99 not found"));
    }
}