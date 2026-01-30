package com.bidly.userservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.exception.ResourceExistsException;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.userservice.dto.AuthRequest;
import com.bidly.userservice.dto.LoginResponseDTO;
import com.bidly.userservice.dto.RegisterUserRequestDTO;
import com.bidly.userservice.entity.User;
import com.bidly.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KeycloakAdminService keycloakAdminService;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

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

        User user = User.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .country("US")
                .build();

        userRepository.save(user);

        return ApiResponse.success(keycloakId, "User registered successfully");
    }

    public ApiResponse<LoginResponseDTO> login(AuthRequest request) {
        // 1. Prepare Request to Keycloak Token Endpoint
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

            System.err.println("Keycloak Error Body: " + e.getResponseBodyAsString());
            throw new RuntimeException("Login failed: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Internal login error");
        }
    }

    public ApiResponse<List<User>> listUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new ResourceNotFoundException("No users found");
        }
        return ApiResponse.success(users, "Users retrieved successfully");
    }
}