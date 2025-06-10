package com.cts.attendance_management.controller;

import com.cts.attendance_management.dto.AttendanceReportDto;
import com.cts.attendance_management.entity.enums.AttendanceReportType;
import com.cts.attendance_management.exception.ResourceNotFoundException;
import com.cts.attendance_management.service.AttendanceReportService;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceReportController.class)
public class AttendanceReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AttendanceReportService attendanceReportService;

    @Autowired
    private ObjectMapper objectMapper; // Autowire ObjectMapper directly

    private AttendanceReportDto weeklyReportDto;
    private AttendanceReportDto monthlyReportDto;
    private AttendanceReportDto yearlyReportDto;
    private AttendanceReportDto customReportDto;


    @BeforeEach
    void setup() {
        // Using a fixed date for consistent test results
        LocalDate today = LocalDate.of(2025, 6, 10); // Tuesday, June 10, 2025

        // Define specific dates for reports to make tests deterministic
        // Weekly report (Mon-Sun for the week of June 10, 2025)
        LocalDate startOfWeek = LocalDate.of(2025, 6, 9);  // Monday, June 9, 2025
        LocalDate endOfWeek = LocalDate.of(2025, 6, 15);   // Sunday, June 15, 2025

        // Monthly report (June 2025)
        LocalDate startOfMonth = LocalDate.of(2025, 6, 1);
        LocalDate endOfMonth = LocalDate.of(2025, 6, 30);

        // Yearly report (2025)
        LocalDate startOfYear = LocalDate.of(2025, 1, 1);
        LocalDate endOfYear = LocalDate.of(2025, 12, 31);

        // --- Initialize DTOs with updated constructor (8 arguments) ---
        // Format: id, startDate, endDate, totalPresent, totalAbsent, totalWorkingDays, type, employeeId
        weeklyReportDto = new AttendanceReportDto(
                1L, startOfWeek, endOfWeek, 5, 0, 5, AttendanceReportType.WEEKLY, 1L
        );
        monthlyReportDto = new AttendanceReportDto(
                2L, startOfMonth, endOfMonth, 20, 1, 21, AttendanceReportType.MONTHLY, 1L
        );
        yearlyReportDto = new AttendanceReportDto(
                3L, startOfYear, endOfYear, 240, 20, 260, AttendanceReportType.YEARLY, 1L
        );
        // Assuming AttendanceReportType.CUSTOM exists. If not, use 'null' instead of AttendanceReportType.CUSTOM.
        customReportDto = new AttendanceReportDto(
                4L, LocalDate.of(2025, 5, 1), LocalDate.of(2025, 5, 15), 10, 1, 11, AttendanceReportType.CUSTOM, 1L
        );
    }

    @Test
    void getAllAttendanceReports_shouldReturnListOfReports() throws Exception {
        // Arrange
        List<AttendanceReportDto> expectedReports = List.of(weeklyReportDto, monthlyReportDto, yearlyReportDto);
        when(attendanceReportService.getAllReports()).thenReturn(expectedReports);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/attendance-reports"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<AttendanceReportDto> actualReports = jsonToAttendanceReportDtoList(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualReports, is(expectedReports));
    }

    @Test
    void getReportsByEmployee_shouldReturnListOfReportsForEmployee() throws Exception {
        // Arrange
        Long employeeId = 1L;
        List<AttendanceReportDto> expectedReports = List.of(weeklyReportDto, monthlyReportDto, yearlyReportDto);
        when(attendanceReportService.getReportsByEmployee(employeeId)).thenReturn(expectedReports);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<AttendanceReportDto> actualReports = jsonToAttendanceReportDtoList(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualReports, is(expectedReports));
    }

    @Test
    void getReportsByEmployee_shouldReturnNotFound_whenEmployeeDoesNotExist() throws Exception {
        // Arrange
        Long nonExistentEmployeeId = 99L;
        when(attendanceReportService.getReportsByEmployee(nonExistentEmployeeId))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: " + nonExistentEmployeeId));

        // Act & Assert
        mockMvc.perform(get("/api/attendance-reports/employee/" + nonExistentEmployeeId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getReportsByEmployeeAndType_shouldReturnWeeklyReport() throws Exception {
        // Arrange
        Long employeeId = 1L;
        String type = "WEEKLY";
        List<AttendanceReportDto> expectedReports = List.of(weeklyReportDto);
        when(attendanceReportService.getReportsByEmployeeAndType(employeeId, type)).thenReturn(expectedReports);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId + "/type/" + type))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<AttendanceReportDto> actualReports = jsonToAttendanceReportDtoList(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualReports, is(expectedReports));
    }

    @Test
    void getReportsByEmployeeAndType_shouldReturnMonthlyReport() throws Exception {
        // Arrange
        Long employeeId = 1L;
        String type = "MONTHLY";
        List<AttendanceReportDto> expectedReports = List.of(monthlyReportDto);
        when(attendanceReportService.getReportsByEmployeeAndType(employeeId, type)).thenReturn(expectedReports);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId + "/type/" + type))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<AttendanceReportDto> actualReports = jsonToAttendanceReportDtoList(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualReports, is(expectedReports));
    }

    @Test
    void getReportsByEmployeeAndType_shouldReturnYearlyReport() throws Exception {
        // Arrange
        Long employeeId = 1L;
        String type = "YEARLY";
        List<AttendanceReportDto> expectedReports = List.of(yearlyReportDto);
        when(attendanceReportService.getReportsByEmployeeAndType(employeeId, type)).thenReturn(expectedReports);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId + "/type/" + type))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<AttendanceReportDto> actualReports = jsonToAttendanceReportDtoList(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualReports, is(expectedReports));
    }

    @Test
    void getReportsByEmployeeAndType_shouldReturnBadRequest_whenInvalidType() throws Exception {
        // Arrange
        Long employeeId = 1L;
        String invalidType = "INVALID_TYPE";
        when(attendanceReportService.getReportsByEmployeeAndType(employeeId, invalidType))
                .thenThrow(new IllegalArgumentException("Invalid report type: " + invalidType));

        // Act & Assert
        mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId + "/type/" + invalidType))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCustomReportByEmployee_shouldReturnCustomReport() throws Exception {
        // Arrange
        Long employeeId = 1L;
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 15);
        when(attendanceReportService.getCustomReportByEmployee(employeeId, startDate, endDate)).thenReturn(customReportDto);

        // Act & Assert
        MvcResult mvcResult = mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId + "/custom")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        AttendanceReportDto actualReport = jsonToAttendanceReportDto(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualReport, is(customReportDto));
    }

    @Test
    void getCustomReportByEmployee_shouldReturnNotFound_whenEmployeeDoesNotExist() throws Exception {
        // Arrange
        Long nonExistentEmployeeId = 99L;
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 5, 15);
        when(attendanceReportService.getCustomReportByEmployee(nonExistentEmployeeId, startDate, endDate))
                .thenThrow(new ResourceNotFoundException("Employee not found with id: " + nonExistentEmployeeId));

        // Act & Assert
        mockMvc.perform(get("/api/attendance-reports/employee/" + nonExistentEmployeeId + "/custom")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomReportByEmployee_shouldReturnBadRequest_whenInvalidDates() throws Exception {
        // Arrange - Example of invalid date range (endDate before startDate)
        Long employeeId = 1L;
        LocalDate startDate = LocalDate.of(2025, 5, 15);
        LocalDate endDate = LocalDate.of(2025, 5, 1);
        when(attendanceReportService.getCustomReportByEmployee(employeeId, startDate, endDate))
                .thenThrow(new IllegalArgumentException("End date cannot be before start date."));

        // Act & Assert
        mockMvc.perform(get("/api/attendance-reports/employee/" + employeeId + "/custom")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isBadRequest());
    }

    // Helper methods for JSON deserialization
    private AttendanceReportDto jsonToAttendanceReportDto(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, AttendanceReportDto.class);
    }

    private List<AttendanceReportDto> jsonToAttendanceReportDtoList(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, new TypeReference<List<AttendanceReportDto>>() {});
    }
}