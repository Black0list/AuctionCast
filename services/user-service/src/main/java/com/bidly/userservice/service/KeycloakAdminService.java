package com.bidly.userservice.service;

import com.bidly.common.exception.ResourceExistsException;
import com.bidly.userservice.dto.RegisterUserRequestDTO;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final String realm;

    public KeycloakAdminService(Keycloak keycloak, @Value("${keycloak.realm}") String realm) {
        this.keycloak = keycloak;
        this.realm = realm;
    }

    public String registerUser(RegisterUserRequestDTO request) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));


        UsersResource usersResource = keycloak.realm(realm).users();
        Response response = usersResource.create(user);

        if (response.getStatus() == 409) {
            throw new ResourceExistsException("Email already used");
        } else if(response.getStatus() != 201) {
            throw new IllegalArgumentException("Failed to create user: " + response.getStatusInfo());
        }

        String userId = CreatedResponseUtil.getCreatedId(response);

        assignRole(userId, "USER");

        return userId;
    }

    private void assignRole(String userId, String roleName) {
        try {
            RoleRepresentation role = keycloak.realm(realm)
                    .roles()
                    .get(roleName)
                    .toRepresentation();

            keycloak.realm(realm)
                    .users()
                    .get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(role));
        } catch (Exception e) {
            System.err.println("Warning: Could not assign role " + roleName + " to user " + userId + ": " + e.getMessage());
        }
    }

    public void updateUser(String keycloakId, String email, String firstName, String lastName, Boolean enabled) {
        UserRepresentation user = keycloak.realm(realm).users().get(keycloakId).toRepresentation();

        if (email != null) {
            user.setEmail(email);
            user.setUsername(email);
        }
        if (firstName != null) user.setFirstName(firstName);
        if (lastName != null) user.setLastName(lastName);
        if (enabled != null) user.setEnabled(enabled);

        keycloak.realm(realm).users().get(keycloakId).update(user);
    }

    public void deleteUser(String keycloakId) {
        keycloak.realm(realm).users().get(keycloakId).remove();
    }
}