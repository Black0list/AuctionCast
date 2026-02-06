package com.bidly.userservice.dto;

import com.bidly.common.enums.SellerStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserDTO {
    private String id;
    private String email;
    private String photo;
    private String firstName;
    private String phone;
    private String lastName;
    private SellerStatus sellerStatus;
    private boolean isActive;
    private LocalDateTime lastLoginAt;
}
