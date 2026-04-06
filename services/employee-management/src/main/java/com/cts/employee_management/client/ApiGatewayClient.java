package com.cts.employee_management.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name="api-gateway")
public interface ApiGatewayClient {
    @PostMapping("auth/create-auth/{employeeId}/{email}")
    void createAuth(@PathVariable Long employeeId,  @PathVariable String email);
}
