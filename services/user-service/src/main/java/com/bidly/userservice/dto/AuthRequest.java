package com.bidly.userservice.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class AuthRequest {
    @Email
    private String email;
    private String password;
}
