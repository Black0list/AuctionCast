package com.bidly.userservice.mapper;

import com.bidly.userservice.dto.RegisterUserRequestDTO;
import com.bidly.userservice.dto.UserDTO;
import com.bidly.userservice.dto.UserUpdateDTO;
import com.bidly.userservice.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public static UserDTO toDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .photo(user.getPhoto())
                .phone(user.getPhone())
                .isActive(user.isActive())
                .sellerStatus(user.getSellerStatus())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    public static User toEntity(RegisterUserRequestDTO dto) {
        return User.builder()
                .email(dto.getEmail())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .photo("https://www.gravatar.com/avatar/?d=identicon")
                .phone("Not set")
                .isActive(true)
                .sellerStatus(com.bidly.common.enums.SellerStatus.NONE)
                .lastLoginAt(LocalDateTime.now())
                .build();
    }

    public static User patchEntity(User user, UserUpdateDTO updateDto) {
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        if (updateDto.getPhoto() != null) {
            user.setPhoto(updateDto.getPhoto());
        }
        if (updateDto.getPhone() != null) {
            user.setPhone(updateDto.getPhone());
        }
        return user;
    }
}
