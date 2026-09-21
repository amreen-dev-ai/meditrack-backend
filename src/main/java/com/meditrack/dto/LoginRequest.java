package com.meditrack.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
