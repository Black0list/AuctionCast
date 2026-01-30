package com.bidly.userservice.service;


import com.bidly.common.dto.ApiResponse;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.userservice.dto.UserDTO;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.mapper.UserMapper;
import com.bidly.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;

    public ApiResponse<List<UserDTO>> listUsers() {
        List<UserDTO> users = userRepository.findAll().stream().map(UserMapper::toDto).toList();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return ApiResponse.success(users, "Users retrieved successfully");
    }
}
