package com.cts.api_gateway.service;

import com.cts.api_gateway.client.EmployeeClient;
import com.cts.api_gateway.dto.EmployeeAuthDto;
import com.cts.api_gateway.entity.AuthUser;
import com.cts.api_gateway.repository.AuthRepository;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AuthRepository authRepository;

    @Autowired
    private EmployeeClient employeeClient;

    @Override
    public UserDetails loadUserByUsername(String email){
        EmployeeAuthDto employee = employeeClient.loadEmployeeByEmail(email);
        System.out.println("statement 2");
        AuthUser user = authRepository.findByEmployeeId(employee.getId())
                .orElseThrow(()->new AuthenticationServiceException("Cannot find password!"));
        String role = employee.getRole();
        return new User(email, user.getPassword(),
                List.of(new SimpleGrantedAuthority(role)));
    }
}
