package com.bidly.coreservice.dto.order;

import com.bidly.common.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class OrderResponseDTO {
    private UUID id;
    private UUID auctionId;
    private UUID productId;
    private String sellerId;
    private String buyerId;
    private BigDecimal amount;

    private String shippingFullName;
    private String shippingPhone;
    private String shippingAddress;
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