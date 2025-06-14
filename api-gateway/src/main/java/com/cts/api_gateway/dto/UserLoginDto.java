package com.cts.api_gateway.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
