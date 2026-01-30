package com.bidly.userservice.dto;


import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDTO {
    @Email
    private String email;
    private String firstName;
    private String lastName;
    private String photo;
    private String phone;
}
