package com.bidly.userservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.enums.SellerStatus;
import com.bidly.userservice.cache.UserCacheWriter;
import com.bidly.userservice.client.CatalogClient;
import com.bidly.userservice.client.CoreClient;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private UserCacheWriter userCacheWriter;
    @Mock private KeycloakAdminService keycloakAdminService;
    @Mock private CatalogClient catalogClient;
    @Mock private CoreClient coreClient;

    @InjectMocks
    private AdminService adminService;

    private User testUser;
    private final String userId = "user-123";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setKeycloakId(userId);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setActive(true);
    }

    @Test
    void approveSeller_ShouldSucceed_WhenPending() {
        testUser.setSellerStatus(SellerStatus.PENDING);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Void> response = adminService.approveSeller(userId);

        assertEquals(SellerStatus.APPROVED, testUser.getSellerStatus());
        verify(keycloakAdminService).assignRole(userId, "SELLER");
        verify(userRepository).save(testUser);
        verify(userCacheWriter).putPublicProfile(testUser);
        assertTrue(response.isSuccess());
    }

    @Test
    void approveSeller_ShouldThrowException_WhenNotPending() {
        testUser.setSellerStatus(SellerStatus.APPROVED);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> adminService.approveSeller(userId));
    }

    @Test
    void rejectSeller_ShouldSucceed_WhenPending() {
        testUser.setSellerStatus(SellerStatus.PENDING);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Void> response = adminService.rejectSeller(userId);

        assertEquals(SellerStatus.REJECTED, testUser.getSellerStatus());
        verify(userRepository).save(testUser);
        assertTrue(response.isSuccess());
    }

    @Test
    void updateUserStatus_ShouldUpdateActiveState() {
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        adminService.updateUserStatus(userId, false);

        assertFalse(testUser.isActive());
        verify(keycloakAdminService).updateUser(userId, null, null, null, false);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_SoftDelete_ShouldDeactivateUser() {
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Void> response = adminService.deleteUser(userId, false);

        assertFalse(testUser.isActive());
        verify(keycloakAdminService).updateUser(userId, null, null, null, false);
        verify(userRepository).save(testUser);
        assertTrue(response.isSuccess());
    }

    @Test
    void deleteUser_HardDelete_ShouldRemoveUser() {
        testUser.setActive(false);
        when(userRepository.findByKeycloakId(userId)).thenReturn(Optional.of(testUser));

        ApiResponse<Void> response = adminService.deleteUser(userId, true);

        verify(catalogClient).deleteUserProducts(userId);
        verify(coreClient).deleteUserData(userId);
        verify(userRepository).delete(testUser);
        verify(keycloakAdminService).deleteUser(userId);
        assertTrue(response.isSuccess());
    }
}
