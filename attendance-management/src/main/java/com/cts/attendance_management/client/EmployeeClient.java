package com.cts.attendance_management.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(url= "http://localhost:9191",name="employee-management")
public interface EmployeeClient {
    @GetMapping("api/employees/{id}/exists")
    boolean checkEmployeeExists(@PathVariable Long id);
}
