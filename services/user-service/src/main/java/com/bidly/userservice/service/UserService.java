package com.bidly.userservice.service;


import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.userservice.mapper.UserMapper;
import com.bidly.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ApiResponse<Boolean> isSeller(String userId){

        Boolean exists = userRepository.existsByKeycloakId(userId);

        return ApiResponse.success(exists, "User status returned");
    }

    public ApiResponse<List<UserPublicDTO>> batchProfiles(List<String> missingIds) {

        List<UserPublicDTO> profiles = missingIds.stream()
                .map(userRepository::findByKeycloakId)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(UserMapper::toPublicDto)
                .collect(Collectors.toList());

        return ApiResponse.success(profiles, "Batch profiles fetched successfully");
    }

    public ApiResponse<UserPublicDTO> findOne(String userId) {
        UserPublicDTO user = userRepository.findByKeycloakId(userId)
                .map(UserMapper::toPublicDto)
                .orElse(null);

        return ApiResponse.success(user, "User retrieved successfully");
    }
}
