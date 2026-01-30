package com.bidly.userservice.controller;


import com.bidly.common.dto.ApiResponse;
import com.bidly.userservice.dto.UserDTO;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    public final AdminService adminService;

    @RequestMapping("/users")
    public ApiResponse<List<UserDTO>> list() {
        return adminService.listUsers();
    }
}
