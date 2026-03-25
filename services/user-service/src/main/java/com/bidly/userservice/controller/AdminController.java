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
    public ApiResponse<UserDTO> updateUser(@PathVariable("id") String id, @Valid @ModelAttribute UserAdminUpdateDTO updateDto) {
        return adminService.updateUser(id, updateDto);
    }

    @DeleteMapping("/users/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable("id") String id, @RequestParam(value = "hardDelete", defaultValue = "true") boolean hardDelete) {
        return adminService.deleteUser(id, hardDelete);
    }

    @PostMapping("/users/{id}/approve-seller")
    public ApiResponse<Void> approveSeller(@PathVariable("id") String id) {
        return adminService.approveSeller(id);
    }

    @PostMapping("/users/{id}/reject-seller")
    public ApiResponse<Void> rejectSeller(@PathVariable("id") String id) {
        return adminService.rejectSeller(id);
    }

    @GetMapping("/users/count")
    public ApiResponse<Long> countUsers() {
        return adminService.countAllUsers();
    }

    @PatchMapping("/users/{id}/status")
    public ApiResponse<Void> updateStatus(@PathVariable("id") String id, @RequestParam("isActive") boolean isActive) {
        return adminService.updateUserStatus(id, isActive);
    }
}
