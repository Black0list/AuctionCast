package com.bidly.userservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.userservice.dto.*;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.service.AuthService;
import com.bidly.userservice.service.UserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserSyncService userSyncService;

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterUserRequestDTO request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponseDTO> login(@RequestBody AuthRequest request) {
        return authService.login(request);
    }

    @GetMapping("/users/me")
    public ApiResponse<UserDTO> getProfile(@AuthenticationPrincipal Jwt jwt) {
        User user = userSyncService.sync(jwt);
        return authService.getProfile(user.getId());
    }

    @PatchMapping("/me")
    public ApiResponse<UserDTO> getProfile(@AuthenticationPrincipal Jwt jwt, @RequestBody UserUpdateDTO updateDto) {
        ApiResponse<UserDTO> response = authService.updateUser(jwt, updateDto);
        userSyncService.sync(jwt);
        return response;
    }

}