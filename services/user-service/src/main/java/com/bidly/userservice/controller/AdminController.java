package com.bidly.userservice.controller;


import com.bidly.common.dto.ApiResponse;
import com.bidly.userservice.dto.UserAdminUpdateDTO;
import com.bidly.userservice.dto.UserDTO;
import com.bidly.userservice.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    public final AdminService adminService;

    @GetMapping("/users")
    public ApiResponse<List<UserDTO>> list() {
        return adminService.listUsers();
    }

    @PatchMapping(value = "/users/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<UserDTO> updateUser(@PathVariable String id, @Valid @ModelAttribute UserAdminUpdateDTO updateDto) {
        return adminService.updateUser(id, updateDto);
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable String id, @RequestParam(defaultValue = "false") boolean hardDelete) {
        return adminService.deleteUser(id, hardDelete);
    }
}
