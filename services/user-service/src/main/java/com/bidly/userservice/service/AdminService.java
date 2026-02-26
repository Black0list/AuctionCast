package com.bidly.userservice.service;


import com.bidly.common.dto.ApiResponse;
import com.bidly.common.enums.SellerStatus;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.userservice.cache.UserCacheWriter;
import com.bidly.userservice.dto.UserAdminUpdateDTO;
import com.bidly.userservice.dto.UserDTO;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.mapper.UserMapper;
import com.bidly.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final UserCacheWriter userCacheWriter;
    private final KeycloakAdminService keycloakAdminService;

    public ApiResponse<List<UserDTO>> listUsers() {
        List<UserDTO> users = userRepository.findAll().stream().map(UserMapper::toDto).toList();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return ApiResponse.success(users, "Users retrieved successfully");
    }

    public ApiResponse<UserDTO> updateUser(String id, UserAdminUpdateDTO updateDto) {
        User user = userRepository.findByKeycloakId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String imageUrl = Optional.ofNullable(updateDto.getPhoto())
                .filter(f -> !f.isEmpty())
                .map(f -> fileStorageService.saveFile(f, "users-photos/"))
                .orElse(user.getPhoto());

        if (updateDto.getPhoto() != null && !updateDto.getPhoto().isEmpty() && user.getPhoto() != null && !user.getPhoto().equals("https://www.gravatar.com/avatar/?d=identicon")) {
            fileStorageService.deleteFile(user.getPhoto());
        }

        UserMapper.patchEntity(user, updateDto, imageUrl);

        keycloakAdminService.updateUser(
                user.getKeycloakId(),
                updateDto.getEmail(),
                updateDto.getFirstName(),
                updateDto.getLastName(),
                updateDto.getIsActive()
        );

        userRepository.save(user);
        userCacheWriter.putPublicProfile(user);

        return ApiResponse.success(UserMapper.toDto(user), "User updated successfully");
    }

    public ApiResponse<Void> deleteUser(String id, boolean hardDelete) {
        User user = userRepository.findByKeycloakId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (hardDelete) {
            if (user.getSellerStatus() == SellerStatus.APPROVED && user.isActive()) {
                return ApiResponse.error("Cannot hard delete an approved and active seller. Please deactivate the account or use soft delete first.");
            }

            try {
                userRepository.delete(user);

                keycloakAdminService.deleteUser(user.getKeycloakId());
                if (user.getPhoto() != null) {
                    fileStorageService.deleteFile(user.getPhoto());
                }
                userCacheWriter.evictPublicProfile(user.getKeycloakId());

                return ApiResponse.success(null, "User permanently deleted from system");
            } catch (DataIntegrityViolationException e) {
                return ApiResponse.error("Cannot hard delete user: This account is linked to other records (like auctions, orders, or history). Use soft delete instead to preserve data integrity.");
            } catch (Exception e) {
                return ApiResponse.error("An error occurred while hard deleting the user: " + e.getMessage());
            }
        } else {
            user.setActive(false);
            keycloakAdminService.updateUser(user.getKeycloakId(), null, null, null, false);
            userRepository.save(user);
            userCacheWriter.putPublicProfile(user);
            return ApiResponse.success(null, "User account successfully deactivated (soft delete)");
        }
    }
}
