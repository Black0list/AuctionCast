package com.bidly.userservice.dto;

import lombok.Data;

@Data
public class RegisterUserRequestDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}