package com.cts.api_gateway.config;

import com.cts.api_gateway.security.JwtFilter;
import com.cts.api_gateway.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Autowired
    JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("api/leave-requests/**").hasAuthority("ADMIN")
                .anyRequest().permitAll());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.formLogin(f -> f.disable());
        http.csrf(c -> c.disable());
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(){
        return new CustomUserDetailsService();
    }

    @Bean
    public AuthenticationManager authenticationManager(){
        return new ProviderManager(List.of(authenticationProvider()));
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
