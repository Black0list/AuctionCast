package com.bidly.userservice.controller;


import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/is-seller")
    public ApiResponse<Boolean> isSeller(@PathVariable("userId") String userId){
        return userService.isSeller(userId);
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserPublicDTO> findOne(@PathVariable("userId") String userId){
        return userService.findOne(userId);
    }

    @PostMapping("/batch-public-profiles")
    public ApiResponse<List<UserPublicDTO>> batchProfiles(@RequestBody List<String> missingIds){
        return userService.batchProfiles(missingIds);
    }

    @PostMapping("/apply-seller")
    public ApiResponse<Void> applyToBeSeller(@AuthenticationPrincipal Jwt jwt) {
        return userService.applyToBeSeller(jwt.getSubject());
    }
}
