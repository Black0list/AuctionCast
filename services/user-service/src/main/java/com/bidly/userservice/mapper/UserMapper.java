package com.bidly.userservice.mapper;

import com.bidly.common.dto.UserPublicDTO;
import com.bidly.userservice.dto.RegisterUserRequestDTO;
import com.bidly.userservice.dto.UserDTO;
import com.bidly.userservice.dto.UserAdminUpdateDTO;
import com.bidly.userservice.dto.UserUpdateDTO;
import com.bidly.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getKeycloakId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .photo(user.getPhoto())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .sellerStatus(user.getSellerStatus())
                .addressLine1(user.getAddressLine1())
                .addressLine2(user.getAddressLine2())
                .city(user.getCity())
                .state(user.getState())
                .postalCode(user.getPostalCode())
                .country(user.getCountry())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public static User toEntity(RegisterUserRequestDTO dto, String KeycloakId) {
        return User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .photo("https://www.gravatar.com/avatar/?d=identicon")
                .phone("Not set")
                .isActive(true)
                .keycloakId(KeycloakId)
                .sellerStatus(com.bidly.common.enums.SellerStatus.NONE)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    public static User patchEntity(User user, UserUpdateDTO updateDto, String imageUrl) {
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (imageUrl != null) {
            user.setPhoto(imageUrl);
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getAddressLine1() != null) {
            user.setAddressLine1(updateDto.getAddressLine1());
        }
        if (updateDto.getAddressLine2() != null) {
            user.setAddressLine2(updateDto.getAddressLine2());
        }
        if (updateDto.getCity() != null) {
            user.setCity(updateDto.getCity());
        }
        if (updateDto.getState() != null) {
            user.setState(updateDto.getState());
        }
        if (updateDto.getPostalCode() != null) {
            user.setPostalCode(updateDto.getPostalCode());
        }
        if (updateDto.getCountry() != null) {
            user.setCountry(updateDto.getCountry());
        }
        return user;
    }

    public static User patchEntity(User user, UserAdminUpdateDTO updateDto, String imageUrl) {
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (imageUrl != null) {
            user.setPhoto(imageUrl);
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        user.setActive(updateDto.getIsActive());
        if (updateDto.getSellerStatus() != null) {
            user.setSellerStatus(updateDto.getSellerStatus());
        }
        return user;
    }

    public static UserPublicDTO toPublicDto(User user){
        return UserPublicDTO.builder()
                .id(user.getKeycloakId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .photo(user.getPhoto())
                .addressLine1(user.getAddressLine1())
                .addressLine2(user.getAddressLine2())
                .city(user.getCity())
                .state(user.getState())
                .postalCode(user.getPostalCode())
                .country(user.getCountry())
                .build();
    }
}
