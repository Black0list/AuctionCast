package com.bidly.userservice.service;


import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.mapper.UserMapper;
import com.bidly.userservice.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.bidly.common.enums.SellerStatus;


@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ApiResponse<Boolean> isSeller(String userId){

        User user = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isSeller = user.getSellerStatus() == SellerStatus.APPROVED;

        return ApiResponse.success(isSeller, "User status returned");
    }

    public ApiResponse<Void> applyToBeSeller(String userId) {
        User user = userRepository.findByKeycloakId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.getSellerStatus() == SellerStatus.PENDING) {
            throw new IllegalArgumentException("You already have a pending application");
        }

        if (user.getSellerStatus() == SellerStatus.APPROVED) {
            throw new IllegalArgumentException("You are already an approved seller");
        }

        user.setSellerStatus(SellerStatus.PENDING);
        userRepository.save(user);

        return ApiResponse.success(null, "Seller application submitted successfully");
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
