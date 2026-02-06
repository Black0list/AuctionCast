package com.bidly.userservice.repository;

import com.bidly.common.dto.ApiResponse;
import com.bidly.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByKeycloakId(String keycloakId);
    boolean existsByEmail(String email);
    boolean existsByKeycloakId(String userId);
}