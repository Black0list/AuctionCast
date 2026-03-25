package com.bidly.userservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.userservice.dto.*;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.service.AuthService;
import com.bidly.userservice.service.UserSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
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

    @PostMapping("/refresh")
    public ApiResponse<LoginResponseDTO> refresh(@RequestParam("refreshToken") String refreshToken) {
        return authService.refreshToken(refreshToken);
    }

    @GetMapping("/users/me")
    public ApiResponse<UserDTO> getProfile(@AuthenticationPrincipal Jwt jwt) {
        User user = userSyncService.sync(jwt);
        return authService.getProfile(user.getKeycloakId());
    }

    @PatchMapping(value = "/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserDTO> updateProfile(@AuthenticationPrincipal Jwt jwt, @Valid @ModelAttribute UserUpdateDTO updateDto) {
        return authService.updateUser(jwt, updateDto);
    }

}