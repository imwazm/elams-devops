package com.cts.employee_management.controller;

import com.cts.employee_management.dto.ShiftRequestDto;
import com.cts.employee_management.dto.ShiftResponseDto;
import com.cts.employee_management.exception.ErrorResponseEntity;
import com.cts.employee_management.exception.ResourceNotFoundException;
import com.cts.employee_management.service.ShiftService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalTime;
import java.util.List;

import static com.cts.employee_management.entity.enums.ShiftType.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShiftController.class)
public class ShiftControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShiftService shiftService;

    @Autowired
    ObjectMapper objectMapper;

    private ShiftResponseDto morningShiftResponseDto, eveningShiftResponseDto;
    private ShiftRequestDto morningShiftRequestDto, updatedMorningShiftRequestDto;

    @BeforeEach
    void setup() {
        morningShiftRequestDto = new ShiftRequestDto(
                LocalTime.of(8, 0, 0),
                LocalTime.of(16, 0, 0),
                MORNING
        );

        updatedMorningShiftRequestDto = new ShiftRequestDto(
                LocalTime.of(9, 0, 0),
                LocalTime.of(17, 0, 0),
                GENERAL
        );

        morningShiftResponseDto = new ShiftResponseDto(
                1L,
                LocalTime.of(8, 0, 0),
                LocalTime.of(16, 0, 0),
                MORNING
        );

        eveningShiftResponseDto = new ShiftResponseDto(
                2L,
                LocalTime.of(16, 0, 0),
                LocalTime.of(0, 0, 0),
                EVENING
        );
    }

    @Test
    void getAllShifts_shouldReturnListOfShifts() throws Exception {
        List<ShiftResponseDto> expectedShiftResponseDtoList = List.of(morningShiftResponseDto, eveningShiftResponseDto);
        when(shiftService.getAllShifts()).thenReturn(expectedShiftResponseDtoList);

        MvcResult mvcResult = mockMvc.perform(get("/api/shifts/all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        List<ShiftResponseDto> shiftResponseDtoList = objectMapper
                .readValue(jsonResponse, new TypeReference<List<ShiftResponseDto>>() {});
        assertThat(shiftResponseDtoList, is(expectedShiftResponseDtoList));
    }

    @Test
    void getShiftById_shouldReturnTheShiftWithProvidedId() throws Exception {
        Long id = 1L;
        when(shiftService.getShiftById(id)).thenReturn(morningShiftResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/api/shifts/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShiftResponseDto actualShiftResponseDto = jsonToShiftResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualShiftResponseDto, is(morningShiftResponseDto));
    }

    @Test
    void getShiftById_shouldThrowExceptionWithInvalidId() throws Exception {
        Long id = 99L;
        when(shiftService.getShiftById(id)).thenThrow(
                new ResourceNotFoundException("Shift with id " + id + " not found")
        );

        MvcResult mvcResult = mockMvc.perform(get("/api/shifts/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualErrorResponseEntity = jsonToErrorResponseEntity(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualErrorResponseEntity.getException(), is("ResourceNotFoundException"));
    }

    @Test
    void updateShift_shouldUpdateShiftDetails() throws Exception {
        Long id = 1L;
        ShiftResponseDto expectedUpdatedShiftResponse = new ShiftResponseDto(
                id,
                updatedMorningShiftRequestDto.getStartTime(),
                updatedMorningShiftRequestDto.getEndTime(),
                updatedMorningShiftRequestDto.getType()
        );
        when(shiftService.updateShift(id, updatedMorningShiftRequestDto)).thenReturn(expectedUpdatedShiftResponse);

        MvcResult mvcResult = mockMvc.perform(put("/api/shifts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(updatedMorningShiftRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ShiftResponseDto actualResponse = jsonToShiftResponse(mvcResult.getResponse().getContentAsString());
        assertThat(actualResponse, is(expectedUpdatedShiftResponse));
    }

    @Test
    void updateShift_shouldThrowExceptionWithInvalidId() throws Exception {
        Long id = 99L;
        when(shiftService.updateShift(id, updatedMorningShiftRequestDto)).thenThrow(
                new ResourceNotFoundException("Shift with id " + id + " not found")
        );

        MvcResult mvcResult = mockMvc.perform(put("/api/shifts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(updatedMorningShiftRequestDto)))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualErrorResponseEntity = jsonToErrorResponseEntity(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualErrorResponseEntity.getException(), is("ResourceNotFoundException"));
    }



    private ShiftResponseDto jsonToShiftResponse(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, ShiftResponseDto.class);
    }

    private ErrorResponseEntity jsonToErrorResponseEntity(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, ErrorResponseEntity.class);
    }

    private String toJsonString(ShiftRequestDto shiftRequestDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(shiftRequestDto);
    }
}
