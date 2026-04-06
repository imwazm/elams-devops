package com.cts.api_gateway.controller;

import com.cts.api_gateway.dto.UserLoginDto;
import com.cts.api_gateway.security.JwtUtil;
import com.cts.api_gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("create-auth/{employeeId}/{email}")
    public void createAuth(@PathVariable Long employeeId,  @PathVariable String email){
        authService.createAuth(employeeId, email);
    }

    @PostMapping("login")
    public ResponseEntity<Map<String, String>>login(@RequestBody UserLoginDto user){
        BCryptPasswordEncoder encoder  = new BCryptPasswordEncoder();
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
