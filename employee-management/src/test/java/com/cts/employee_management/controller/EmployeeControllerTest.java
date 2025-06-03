package com.cts.employee_management.controller;

import com.cts.employee_management.dto.EmployeeRequestDto;
import com.cts.employee_management.dto.EmployeeResponseDto;
import com.cts.employee_management.entity.enums.Role;
import com.cts.employee_management.exception.ErrorResponseEntity;
import com.cts.employee_management.exception.ResourceNotFoundException;
import com.cts.employee_management.service.EmployeeService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    ObjectMapper objectMapper;

    private EmployeeResponseDto employeeResponseDto, managerResponseDto, adminResponseDto;
    private EmployeeRequestDto employeeRequestDto,managerRequestDto, adminRequestDto;

    @BeforeEach
    void setup(){

        employeeRequestDto =  new EmployeeRequestDto(null, "Akram", "akram@sample.com");
        managerRequestDto =  new EmployeeRequestDto(null, "Piyush", "piyush@sample.com");
        adminRequestDto =  new EmployeeRequestDto(null, "Rohit", "rohit@sample.com");
        employeeResponseDto = new EmployeeResponseDto(
                1L, "Akram", "akram@sample.com", Role.EMPLOYEE,null,null
        );
        managerResponseDto = new EmployeeResponseDto(
                2L, "Piyush", "piyush@sample.com", Role.MANAGER,null,null
        );
        adminResponseDto = new EmployeeResponseDto(
                3L, "Rohit", "rohit@sample.com", Role.ADMIN,null,null
        );
    }

    @Test
    void getAllEmployees_shouldReturnListOfEmployees() throws Exception {
        List<EmployeeResponseDto> expectedEmployeeResponseDtoList = List.of(employeeResponseDto, managerResponseDto);
        when(employeeService.findAllEmployees()).thenReturn(expectedEmployeeResponseDtoList);

        MvcResult mvcResult = mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        String jsonResponse = mvcResult.getResponse().getContentAsString();

        List<EmployeeResponseDto> employeeResponseDtoList = objectMapper
                .readValue(jsonResponse, new TypeReference<List<EmployeeResponseDto>>(){});
        assertThat(employeeResponseDtoList, is(expectedEmployeeResponseDtoList));
    }

    @Test
    void addEmployee_shouldReturnCreatedEmployee_whenValidInput() throws Exception {
        when(employeeService.addEmployee(employeeRequestDto)).thenReturn(employeeResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/add-employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(employeeRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualEmployeeResponseDto = jsonToEmployeeResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualEmployeeResponseDto, is(employeeResponseDto));
    }

    @Test
    void addManager_shouldReturnCreatedManager_whenValidInput() throws Exception {
        when(employeeService.addManager(managerRequestDto)).thenReturn(managerResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/add-manager")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(managerRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualManagerResponseDto = jsonToEmployeeResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualManagerResponseDto, is(managerResponseDto));
    }

    @Test
    void addAdmin_shouldReturnCreatedManager_whenValidInput() throws Exception {
        when(employeeService.addAdmin(adminRequestDto)).thenReturn(adminResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/add-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(adminRequestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualAdminResponseDto = jsonToEmployeeResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualAdminResponseDto, is(adminResponseDto));
    }

    @Test
    void findEmployeeById_shouldReturnTheEmployeeWithProvidedId() throws Exception {
        Long id = 1L;
        when(employeeService.findEmployeeById(id)).thenReturn(employeeResponseDto);

        MvcResult mvcResult = mockMvc.perform(get("/api/employees/"+id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualEmployeeResponseDto = jsonToEmployeeResponse(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualEmployeeResponseDto, is(employeeResponseDto));
    }

    @Test
    void findEmployeeById_shouldThrowExceptionWithInvalidId() throws Exception {
        Long id = 99L;
        when(employeeService.findEmployeeById(id)).thenThrow(
                new ResourceNotFoundException("")
        );

        MvcResult mvcResult = mockMvc.perform(get("/api/employees/"+id))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualErrorResponseEntity = jsonToErrorResponseEntity(
                mvcResult.getResponse().getContentAsString()
        );
        assertThat(actualErrorResponseEntity.getException(), is("ResourceNotFoundException"));
    }

    @Test
    void promoteEmployee_shouldPromoteEmployeeToManager() throws Exception {
        Long id = 1L;
        employeeResponseDto.setRole(Role.MANAGER);
        when(employeeService.promoteEmployee(id)).thenReturn(employeeResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/" + id + "/promote"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualResponse = jsonToEmployeeResponse(mvcResult.getResponse().getContentAsString());
        assertThat(actualResponse, is(employeeResponseDto));
    }

    @Test
    void promoteEmployee_shouldThrowExceptionIfAlreadyManager() throws Exception {
        Long id = 2L;
        when(employeeService.promoteEmployee(id)).thenThrow(new ResourceNotFoundException(""));

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/" + id + "/promote"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualError = jsonToErrorResponseEntity(mvcResult.getResponse().getContentAsString());
        assertThat(actualError.getException(), is("ResourceNotFoundException"));
    }

    @Test
    void demoteEmployee_shouldDemoteManagerToEmployee() throws Exception {
        Long id = 2L;
        employeeResponseDto.setRole(Role.EMPLOYEE);
        when(employeeService.demoteEmployee(id)).thenReturn(employeeResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/" + id + "/demote"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualResponse = jsonToEmployeeResponse(mvcResult.getResponse().getContentAsString());
        assertThat(actualResponse, is(employeeResponseDto));
    }

    @Test
    void demoteEmployee_shouldThrowExceptionIfAlreadyEmployee() throws Exception {
        Long id = 1L;
        when(employeeService.demoteEmployee(id)).thenThrow(new ResourceNotFoundException(""));

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/" + id + "/demote"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        ErrorResponseEntity actualError = jsonToErrorResponseEntity(mvcResult.getResponse().getContentAsString());
        assertThat(actualError.getException(), is("ResourceNotFoundException"));
    }

    @Test
    void deleteEmployee_shouldRemoveEmployee() throws Exception {
        Long id = 1L;

        mockMvc.perform(post("/api/employees/" + id + "/delete"))
                .andExpect(status().isNoContent())
                .andReturn();
    }

    @Test
    void updateEmployee_shouldUpdateEmployeeDetails() throws Exception {
        Long id = 1L;
        employeeRequestDto.setEmployeeName("Updated Name");
        employeeRequestDto.setEmail("updated@example.com");
        employeeResponseDto.setEmployeeName("Updated Name");
        employeeResponseDto.setEmail("updated@example.com");
        when(employeeService.updateEmployeeDetails(id, employeeRequestDto)).thenReturn(employeeResponseDto);

        MvcResult mvcResult = mockMvc.perform(put("/api/employees/" + id + "/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJsonString(employeeRequestDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualResponse = jsonToEmployeeResponse(mvcResult.getResponse().getContentAsString());
        assertThat(actualResponse, is(employeeResponseDto));
    }

    @Test
    void assignManager_shouldAssignManagerToEmployee() throws Exception {
        Long employeeId = 1L;
        Long managerId = 2L;
        employeeResponseDto.setManagerId(managerId);
        when(employeeService.assignManager(employeeId, managerId)).thenReturn(employeeResponseDto);

        MvcResult mvcResult = mockMvc.perform(post("/api/employees/" + employeeId + "/assign-manager/" + managerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        EmployeeResponseDto actualResponse = jsonToEmployeeResponse(mvcResult.getResponse().getContentAsString());
        assertThat(actualResponse, is(employeeResponseDto));
    }

    @Test
    void findTeamMembers_shouldReturnTeamMembersForManager() throws Exception {
        Long managerId = 2L;
        List<EmployeeResponseDto> teamMembers = List.of(employeeResponseDto);
        when(employeeService.findTeamMembers(managerId)).thenReturn(teamMembers);

        MvcResult mvcResult = mockMvc.perform(get("/api/employees/" + managerId + "/team-members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        List<EmployeeResponseDto> actualResponse = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<EmployeeResponseDto>>() {}
        );
        assertThat(actualResponse, is(teamMembers));
    }

    //helpers
    private EmployeeResponseDto jsonToEmployeeResponse(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, EmployeeResponseDto.class);
    }

    private ErrorResponseEntity jsonToErrorResponseEntity(String jsonString) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, ErrorResponseEntity.class);
    }

    private String toJsonString(EmployeeRequestDto employeeRequestDto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(employeeRequestDto);
    }

}
