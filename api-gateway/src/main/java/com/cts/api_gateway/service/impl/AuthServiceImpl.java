package com.cts.api_gateway.service.impl;

import com.cts.api_gateway.client.EmployeeClient;
import com.cts.api_gateway.dto.EmployeeAuthDto;
import com.cts.api_gateway.entity.AuthUser;
import com.cts.api_gateway.repository.AuthRepository;
import com.cts.api_gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createAuth(String email) {
        EmployeeAuthDto employee = employeeClient.loadEmployeeByEmail(email);
        AuthUser user  = new AuthUser();
        user.setEmployeeId(employee.getId());
        user.setPassword(passwordEncoder.encode(employee.getEmail()));
        authRepository.save(user);
    }
}
