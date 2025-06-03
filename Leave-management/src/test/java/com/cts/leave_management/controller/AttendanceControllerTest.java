package com.cts.leave_management.controller;

import com.cts.leave_management.dto.AttendanceClockInRequestDto;
import com.cts.leave_management.dto.AttendanceClockOutRequestDto;
import com.cts.leave_management.dto.AttendanceResponseDto;
import com.cts.leave_management.entity.enums.AttendanceStatus;
import com.cts.leave_management.exception.ErrorResponseEntity;
import com.cts.leave_management.exception.ResourceNotFoundException;
import com.cts.leave_management.service.AttendanceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AttendanceController.class)
public class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceService attendanceService;

    @Autowired
    ObjectMapper objectMapper;

    private AttendanceClockInRequestDto clockInRequestDto;
    private AttendanceClockOutRequestDto clockOutRequestDto;
    private AttendanceResponseDto attendanceResponseDtoClockIn;
    private AttendanceResponseDto attendanceResponseDtoClockOut;

    @BeforeEach
    void setup() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        LocalDate today = LocalDate.now();
        Long employeeId = 1L;

        clockInRequestDto = new AttendanceClockInRequestDto(LocalTime.of(9, 0), today, employeeId);
        clockOutRequestDto = new AttendanceClockOutRequestDto(LocalTime.of(17, 0), today, employeeId);

        attendanceResponseDtoClockIn = new AttendanceResponseDto(
                1L, LocalTime.of(9, 0), null, 0.0, today, AttendanceStatus.PRESENT, employeeId
        );

        attendanceResponseDtoClockOut = new AttendanceResponseDto(
                1L, LocalTime.of(9, 0), LocalTime.of(17, 0), 8.0, today, AttendanceStatus.PRESENT, employeeId
        );
    }

    @Test
    void getAllAttendance_shouldReturnListOfAttendance() throws Exception {
        List<AttendanceResponseDto> expectedAttendanceList = List.of(attendanceResponseDtoClockIn, attendanceResponseDtoClockOut);
        when(attendanceService.findAllAttendance()).thenReturn(expectedAttendanceList);

        MvcResult mvcResult = mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<AttendanceResponseDto> actualAttendanceList = objectMapper.readValue(
                jsonResponse, new TypeReference<List<AttendanceResponseDto>>() {}
        );
        assertThat(actualAttendanceList, is(expectedAttendanceList));
    }

    @Test
    void getAllAttendance_shouldReturnEmptyList_whenNoAttendanceExists() throws Exception {
        when(attendanceService.findAllAttendance()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/attendance"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void clockIn_shouldReturnCreatedAttendanceResponse_whenValidInput() throws Exception {
        when(attendanceService.clockIn(clockInRequestDto)).thenReturn(attendanceResponseDtoClockIn);

        MvcResult mvcResult = mockMvc.perform(post("/api/attendance/clock-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(clockInRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AttendanceResponseDto actualAttendanceResponseDto = jsonToAttendanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualAttendanceResponseDto, is(attendanceResponseDtoClockIn));
    }

    @Test
    void clockOut_shouldReturnUpdatedAttendanceResponse_whenValidInput() throws Exception {
        when(attendanceService.clockOut(clockOutRequestDto)).thenReturn(attendanceResponseDtoClockOut);

        MvcResult mvcResult = mockMvc.perform(post("/api/attendance/clock-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(clockOutRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AttendanceResponseDto actualAttendanceResponseDto = jsonToAttendanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualAttendanceResponseDto, is(attendanceResponseDtoClockOut));
    }

    @Test
    void deleteAttendance_shouldReturnOkStatus() throws Exception {
        Long id = 1L;
        doNothing().when(attendanceService).deleteAttendance(id);

        mockMvc.perform(delete("/api/attendance/" + id + "/delete"))
                .andExpect(status().isOk());
        verify(attendanceService, times(1)).deleteAttendance(id);
    }

    @Test
    void deleteAttendance_shouldHandleResourceNotFound() throws Exception {
        Long id = 99L;
        doThrow(new ResourceNotFoundException("Attendance not found with id: " + id))
                .when(attendanceService).deleteAttendance(id);

        MvcResult mvcResult = mockMvc.perform(delete("/api/attendance/" + id + "/delete"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualErrorResponseEntity = jsonToErrorResponseEntity(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualErrorResponseEntity.getException(), is("ResourceNotFoundException"));
        assertThat(actualErrorResponseEntity.getMessage(), is("Attendance not found with id: " + id));
    }

    @Test
    void getAttendanceById_shouldReturnTheAttendanceWithProvidedId() throws Exception {
        Long id = 1L;
        when(attendanceService.findAttendanceById(id)).thenReturn(attendanceResponseDtoClockOut);

        MvcResult mvcResult = mockMvc.perform(get("/api/attendance/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AttendanceResponseDto actualAttendanceResponseDto = jsonToAttendanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualAttendanceResponseDto, is(attendanceResponseDtoClockOut));
    }

    @Test
    void getAttendanceById_shouldThrowExceptionWithInvalidId() throws Exception {
        Long id = 99L;
        when(attendanceService.findAttendanceById(id)).thenThrow(
                new ResourceNotFoundException("Attendance not found with id: " + id)
        );

        MvcResult mvcResult = mockMvc.perform(get("/api/attendance/" + id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualErrorResponseEntity = jsonToErrorResponseEntity(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualErrorResponseEntity.getException(), is("ResourceNotFoundException"));
        assertThat(actualErrorResponseEntity.getMessage(), is("Attendance not found with id: " + id));
    }

    private String toJsonString(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    private AttendanceResponseDto jsonToAttendanceResponse(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, AttendanceResponseDto.class);
    }

    private ErrorResponseEntity jsonToErrorResponseEntity(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, ErrorResponseEntity.class);
    }
}