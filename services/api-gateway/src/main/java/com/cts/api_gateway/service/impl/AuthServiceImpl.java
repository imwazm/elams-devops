package com.cts.api_gateway.service.impl;

import com.cts.api_gateway.client.EmployeeClient;
import com.cts.api_gateway.dto.EmployeeAuthDto;
import com.cts.api_gateway.entity.AuthUser;
import com.cts.api_gateway.repository.AuthRepository;
import com.cts.api_gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void createAuth(Long employeeId, String email) {
        AuthUser user  = new AuthUser();
        user.setEmployeeId(employeeId);
        user.setPassword(passwordEncoder.encode(email));
        authRepository.save(user);
    }
}
