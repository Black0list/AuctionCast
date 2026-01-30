package com.bidly.userservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.userservice.dto.AuthRequest;
import com.bidly.userservice.dto.LoginResponseDTO;
import com.bidly.userservice.dto.RegisterUserRequestDTO;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterUserRequestDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @RequestMapping("/users")
    public ApiResponse<List<User>> list() {
        return authService.listUsers();
    }
}