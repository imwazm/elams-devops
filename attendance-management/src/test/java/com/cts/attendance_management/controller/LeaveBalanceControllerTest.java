package com.cts.attendance_management.controller;

import com.cts.attendance_management.dto.LeaveBalanceRequestDto;
import com.cts.attendance_management.dto.LeaveBalanceResponseDto;
import com.cts.attendance_management.entity.enums.LeaveType;
import com.cts.attendance_management.exception.ErrorResponseEntity;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.service.LeaveBalanceService;
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

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveBalanceController.class)
public class LeaveBalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaveBalanceService leaveBalanceService;

    @Autowired
    private ObjectMapper objectMapper;

    private LeaveBalanceRequestDto casualLeaveRequestDto;
    private LeaveBalanceResponseDto casualLeaveResponseDto;
    private LeaveBalanceResponseDto sickLeaveResponseDto;

    @BeforeEach
    void setUp() {

        casualLeaveRequestDto = new LeaveBalanceRequestDto(
                null, LeaveType.CASUAL_LEAVE, 12, 101L
        );
        casualLeaveResponseDto = new LeaveBalanceResponseDto(
                1L, LeaveType.CASUAL_LEAVE, 12, 101L, "Employee One"
        );
        sickLeaveResponseDto = new LeaveBalanceResponseDto(
                2L, LeaveType.SICK_LEAVE, 7, 101L, "Employee One"
        );
    }

    @Test
    void addLeaveBalance_shouldReturnCreatedStatusAndDto_whenValidInput() throws Exception {
        when(leaveBalanceService.addLeaveBalance(any(LeaveBalanceRequestDto.class))).thenReturn(casualLeaveResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/leave-balances")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(casualLeaveRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LeaveBalanceResponseDto actualResponseDto = jsonToLeaveBalanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualResponseDto, is(casualLeaveResponseDto));
    }

    @Test
    void getAllLeaveBalances_shouldReturnListOfAllBalances() throws Exception {
        List<LeaveBalanceResponseDto> expectedList = List.of(casualLeaveResponseDto, sickLeaveResponseDto);
        when(leaveBalanceService.findAllLeaveBalances()).thenReturn(expectedList);

        MvcResult mvcResult = mockMvc.perform(get("/api/leave-balances"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<LeaveBalanceResponseDto> actualList = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LeaveBalanceResponseDto>>() {}
        );
        assertThat(actualList, is(expectedList));
    }

    @Test
    void getLeaveBalanceById_shouldReturnBalance_whenFound() throws Exception {
        Long id = 1L;
        when(leaveBalanceService.findLeaveBalanceById(id)).thenReturn(casualLeaveResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/api/leave-balances/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LeaveBalanceResponseDto actualResponseDto = jsonToLeaveBalanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualResponseDto, is(casualLeaveResponseDto));
    }

    @Test
    void getLeaveBalanceById_shouldReturnNotFound_whenNotFound() throws Exception {
        Long id = 99L;
        when(leaveBalanceService.findLeaveBalanceById(id)).thenThrow(
                new ResourceNotFoundException("Leave balance not found")
        );

        MvcResult mvcResult = mockMvc.perform(get("/api/leave-balances/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualErrorResponseEntity = jsonToErrorResponseEntity(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualErrorResponseEntity.getException(), is("ResourceNotFoundException"));
    }

    @Test
    void getLeaveBalancesByEmployeeId_shouldReturnListOfBalancesForEmployee() throws Exception {
        Long employeeId = 101L;
        List<LeaveBalanceResponseDto> expectedList = List.of(casualLeaveResponseDto, sickLeaveResponseDto);
        when(leaveBalanceService.findLeaveBalancesByEmployeeId(employeeId)).thenReturn(expectedList);

        MvcResult mvcResult = mockMvc.perform(get("/api/leave-balances/employee/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<LeaveBalanceResponseDto> actualList = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<LeaveBalanceResponseDto>>() {}
        );
        assertThat(actualList, is(expectedList));
    }

    @Test
    void updateLeaveBalance_shouldReturnUpdatedBalance() throws Exception {
        Long id = 1L;
        LeaveBalanceRequestDto updateRequestDto = new LeaveBalanceRequestDto(
                id, LeaveType.CASUAL_LEAVE, 10, 101L // Updated balance
        );
        LeaveBalanceResponseDto updatedResponseDto = new LeaveBalanceResponseDto(
                id, LeaveType.CASUAL_LEAVE, 10, 101L, "Employee One"
        );

        when(leaveBalanceService.updateLeaveBalance(eq(id), any(LeaveBalanceRequestDto.class))).thenReturn(updatedResponseDto);

        MvcResult mvcResult = mockMvc.perform(put("/api/leave-balances/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(updateRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LeaveBalanceResponseDto actualResponseDto = jsonToLeaveBalanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualResponseDto, is(updatedResponseDto));
    }

    @Test
    void adjustLeaveBalance_shouldReturnAdjustedBalance_whenApproved() throws Exception {
        Long employeeId = 101L;
        LeaveType leaveType = LeaveType.CASUAL_LEAVE;
        int days = 2;
        boolean isApproved = true;
        LeaveBalanceResponseDto adjustedResponseDto = new LeaveBalanceResponseDto(
                1L, LeaveType.CASUAL_LEAVE, 10, 101L, "Employee One"
        );

        when(leaveBalanceService.adjustLeaveBalance(employeeId, leaveType, days, isApproved))
                .thenReturn(adjustedResponseDto);

        MvcResult mvcResult = mockMvc.perform(patch("/api/leave-balances/adjust")
                        .param("employeeId", String.valueOf(employeeId))
                        .param("leaveType", leaveType.name())
                        .param("days", String.valueOf(days))
                        .param("isApproved", String.valueOf(isApproved)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LeaveBalanceResponseDto actualResponseDto = jsonToLeaveBalanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualResponseDto, is(adjustedResponseDto));
    }

    @Test
    void adjustLeaveBalance_shouldReturnAdjustedBalance_whenRejected() throws Exception {
        Long employeeId = 101L;
        LeaveType leaveType = LeaveType.SICK_LEAVE;
        int days = 3;
        boolean isApproved = false;
        LeaveBalanceResponseDto adjustedResponseDto = new LeaveBalanceResponseDto(
                2L, LeaveType.SICK_LEAVE, 10, 101L, "Employee One"
        );

        when(leaveBalanceService.adjustLeaveBalance(employeeId, leaveType, days, isApproved))
                .thenReturn(adjustedResponseDto);

        MvcResult mvcResult = mockMvc.perform(patch("/api/leave-balances/adjust")
                        .param("employeeId", String.valueOf(employeeId))
                        .param("leaveType", leaveType.name())
                        .param("days", String.valueOf(days))
                        .param("isApproved", String.valueOf(isApproved)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        LeaveBalanceResponseDto actualResponseDto = jsonToLeaveBalanceResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualResponseDto, is(adjustedResponseDto));
    }

    @Test
    void deleteLeaveBalance_shouldReturnNoContent() throws Exception {
        Long id = 1L;
        doNothing().when(leaveBalanceService).deleteLeaveBalance(id);

        mockMvc.perform(delete("/api/leave-balances/{id}", id))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    void initializeLeaveBalancesForNewEmployee_shouldReturnOk() throws Exception {
        Long employeeId = 105L;
        doNothing().when(leaveBalanceService).initializeLeaveBalancesForNewEmployee(employeeId);

        mockMvc.perform(post("/api/leave-balances/initialize/{employeeId}", employeeId))
                .andExpect(status().isOk())
                .andReturn();
    }


    private LeaveBalanceResponseDto jsonToLeaveBalanceResponse(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, LeaveBalanceResponseDto.class);
    }

    private ErrorResponseEntity jsonToErrorResponseEntity(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, ErrorResponseEntity.class);
    }

    private String toJsonString(LeaveBalanceRequestDto leaveBalanceRequestDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(leaveBalanceRequestDto);
    }
}