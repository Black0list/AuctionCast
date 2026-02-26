package com.bidly.coreservice.dto.order;

import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderPublicResponseDTO {
    private UUID id;
    private UUID auctionId;
    private UUID productId;

    private UserPublicDTO seller;
    private UserPublicDTO buyer;
    private ProductPublicDTO product;

    private BigDecimal amount;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;

    private String carrier;
    private String trackingNumber;

    private OrderStatus status;
    private LocalDateTime createdAt;
}