package com.bidly.userservice.service;

import com.bidly.common.enums.SellerStatus;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserSyncService {

    private final UserRepository userRepository;

    public User sync(Jwt jwt) {
        String keycloakId = jwt.getSubject();

        return userRepository.findByKeycloakId(keycloakId)
                .map(user -> updateFromJwt(user, jwt))
                .orElseGet(() -> createFromJwt(jwt));
    }

    private User createFromJwt(Jwt jwt) {
        User user = User.builder()
                .keycloakId(jwt.getSubject())
                .email(jwt.getClaim("email"))
                .firstName(jwt.getClaim("given_name"))
                .lastName(jwt.getClaim("family_name"))
                .sellerStatus(SellerStatus.NONE)
                .lastLoginAt(LocalDateTime.now())
                .build();

        return userRepository.save(user);
    }

    private User updateFromJwt(User user, Jwt jwt) {
        user.setEmail(jwt.getClaim("email"));
        user.setFirstName(jwt.getClaim("given_name"));
        user.setLastName(jwt.getClaim("family_name"));
        user.setLastLoginAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}
