package com.cts.api_gateway.service;

import com.cts.api_gateway.entity.AuthUser;
import com.cts.api_gateway.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        AuthUser user = authRepository.findByEmployeeEmail(email)
                .orElseThrow(()->new UsernameNotFoundException("user not found"));
        String role =  user.getEmployee().getRole().toString();
        return new User(email, user.getPassword(),
                List.of(new SimpleGrantedAuthority(role)));
    }
}
