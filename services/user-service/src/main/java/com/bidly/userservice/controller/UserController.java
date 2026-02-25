package com.bidly.userservice.controller;


import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.userservice.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/is-seller")
    public ApiResponse<Boolean> isSeller(@PathVariable String userId){
        return userService.isSeller(userId);
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserPublicDTO> findOne(@PathVariable String userId){
        return userService.findOne(userId);
    }

    @GetMapping("/batch-public-profiles")
    public ApiResponse<List<UserPublicDTO>> batchProfiles(@RequestBody List<String> missingIds){
        return userService.batchProfiles(missingIds);
    }
}
