package com.cts.api_gateway.controller;

import com.cts.api_gateway.dto.UserLoginDto;
import com.cts.api_gateway.entity.AuthUser;
import com.cts.api_gateway.repository.AuthRepository;
import com.cts.api_gateway.security.JwtUtil;
import com.netflix.discovery.converters.Auto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Optional;

@RestController("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<Map<String, String>>login(@RequestBody UserLoginDto user){
        BCryptPasswordEncoder encoder  = new BCryptPasswordEncoder();
        String val = encoder.encode(user.getPassword());
        try{
            Authentication authentication =  authManager
                    .authenticate(new UsernamePasswordAuthenticationToken(
                            user.getEmail(), user.getPassword()
                    ));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(Map.of("token",token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }
}
