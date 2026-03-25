package com.bidly.userservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.exception.KeycloakException;
import com.bidly.common.exception.ResourceExistsException;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.userservice.cache.UserCacheWriter;
import com.bidly.userservice.dto.*;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.mapper.UserMapper;
import com.bidly.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final FileStorageService fileStorageService;
    private final UserCacheWriter userCacheWriter;


    @Value("${keycloak.server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.public-client-id}")
    private String publicClientId;

    @Value("${keycloak.admin-client-id}")
    private String publicAdminClientId;

    public ApiResponse<String> register(RegisterUserRequestDTO request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("Email already in use");
        }

        String keycloakId = keycloakAdminService.registerUser(request);

        User user = UserMapper.toEntity(request, keycloakId);

        userRepository.save(user);
        userCacheWriter.putPublicProfile(user);

        return ApiResponse.success(keycloakId, "User registered successfully");
    }

    public ApiResponse<LoginResponseDTO> login(AuthRequest request) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", publicClientId);
        map.add("username", request.getEmail());
        map.add("password", request.getPassword());
        map.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();

            LoginResponseDTO loginResponse = new LoginResponseDTO(
                    (String) body.get("access_token"),
                    (String) body.get("refresh_token"),
                    String.valueOf(body.get("expires_in")),
                    String.valueOf(body.get("refresh_expires_in"))
            );
            return ApiResponse.success(loginResponse, "Login successful");

        } catch (HttpClientErrorException e) {

            String body = e.getResponseBodyAsString();
            String errorDescription = "Invalid Credentials";

            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode json = mapper.readTree(body);

                if (json.has("error_description")) {
                    errorDescription = json.get("error_description").asText();
                }
            } catch (Exception ignored) {}

            throw new KeycloakException(errorDescription);
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeycloakException("Internal login error");
        }
    }

    public ApiResponse<LoginResponseDTO> refreshToken(String refreshToken) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakUrl, realm);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", publicClientId);
        map.add("refresh_token", refreshToken);
        map.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(tokenUrl, entity, Map.class);
            Map<String, Object> body = response.getBody();

            LoginResponseDTO loginResponse = new LoginResponseDTO(
                    (String) body.get("access_token"),
                    (String) body.get("refresh_token"),
                    String.valueOf(body.get("expires_in")),
                    String.valueOf(body.get("refresh_expires_in"))
            );
            return ApiResponse.success(loginResponse, "Token refreshed successfully");

        } catch (HttpClientErrorException e) {
            throw new KeycloakException("Invalid or expired refresh token");
        } catch (Exception e) {
            e.printStackTrace();
            throw new KeycloakException("Internal token refresh error");
        }
    }

    public ApiResponse<UserDTO> getProfile(String id) {
        User user = userRepository.findByKeycloakId(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ApiResponse.success(UserMapper.toDto(user), "User profile retrieved successfully");
    }

    public ApiResponse<UserDTO> updateUser(Jwt jwt, UserUpdateDTO updateDto) {
        String keycloakId = jwt.getSubject();

        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String imageUrl = Optional.ofNullable(updateDto.getPhoto())
                .filter(f -> !f.isEmpty())
                .map(f -> fileStorageService.saveFile(f, "users-photos/"))
                .orElse(user.getPhoto());

        if (updateDto.getPhoto() != null && !updateDto.getPhoto().isEmpty()) {
            fileStorageService.deleteFile(user.getPhoto());
        }

        UserMapper.patchEntity(user, updateDto, imageUrl);

        keycloakAdminService.updateUser(
                user.getKeycloakId(),
                updateDto.getEmail(),
                updateDto.getFirstName(),
                updateDto.getLastName(),
                null 
        );

        userRepository.save(user);
        userCacheWriter.putPublicProfile(user);

        return ApiResponse.success(UserMapper.toDto(user),"User updated successfully");
    }
}