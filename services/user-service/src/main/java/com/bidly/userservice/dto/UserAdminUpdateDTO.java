package com.bidly.userservice.dto;


import com.bidly.common.enums.SellerStatus;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminUpdateDTO {

    @Email
    @Size(max = 255, message = "Email must be under 255 characters")
    private String email;

    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Size(max = 20, message = "Phone number must be between 7 and 20 characters")
    private String phone;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @Enumerated(EnumType.STRING)
    private SellerStatus sellerStatus = SellerStatus.NONE;

    private MultipartFile photo;
}
