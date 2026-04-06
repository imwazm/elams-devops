package com.cts.api_gateway.client;

import com.cts.api_gateway.dto.EmployeeAuthDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="employee-management")
public interface EmployeeClient {

    @GetMapping("api/employees/load-employee-by-email/{email}")
    public EmployeeAuthDto loadEmployeeByEmail(@PathVariable String email);

}
