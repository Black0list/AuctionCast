package com.bidly.userservice.entity;

import com.bidly.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // This ID comes from Keycloak (the "sub" claim)
    @Column(unique = true, nullable = false)
    private String keycloakId;

    @Column(unique = true, nullable = false)
    private String email;

    private String firstName;
    private String lastName;

    private String country;
}