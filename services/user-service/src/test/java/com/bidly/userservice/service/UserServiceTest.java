package com.bidly.userservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.SellerStatus;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private final String userId = "user-123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setKeycloakId(userId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@example.com");
    }

    @Test
    void isSeller_ShouldReturnTrue_WhenUserIsApproved() {
        testUser.setSellerStatus(SellerStatus.APPROVED);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Boolean> response = userService.isSeller(userId);

        assertTrue(response.getData());
        assertEquals("User status returned", response.getMessage());
    }

    @Test
    void isSeller_ShouldReturnFalse_WhenUserIsNotApproved() {
        testUser.setSellerStatus(SellerStatus.PENDING);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Boolean> response = userService.isSeller(userId);

        assertFalse(response.getData());
    }

    @Test
    void isSeller_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.isSeller(userId));
    }

    @Test
    void applyToBeSeller_ShouldSucceed_WhenUserHasNoStatus() {
        testUser.setSellerStatus(null);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Void> response = userService.applyToBeSeller(userId);

        assertEquals(SellerStatus.PENDING, testUser.getSellerStatus());
        verify(userRepository).save(testUser);
        assertTrue(response.isSuccess());
    }

    @Test
    void applyToBeSeller_ShouldThrowException_WhenAlreadyPending() {
        testUser.setSellerStatus(SellerStatus.PENDING);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> userService.applyToBeSeller(userId));
    }

    @Test
    void applyToBeSeller_ShouldThrowException_WhenAlreadyApproved() {
        testUser.setSellerStatus(SellerStatus.APPROVED);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> userService.applyToBeSeller(userId));
    }

    @Test
    void findOne_ShouldReturnUser_WhenExists() {
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<UserPublicDTO> response = userService.findOne(userId);

        assertNotNull(response.getData());
        assertEquals(userId, response.getData().getId());
    }

    @Test
    void batchProfiles_ShouldReturnProfiles_WhenUsersExist() {
        List<String> ids = List.of(userId);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<List<UserPublicDTO>> response = userService.batchProfiles(ids);

        assertEquals(1, response.getData().size());
        assertEquals(userId, response.getData().get(0).getId());
    }
}
