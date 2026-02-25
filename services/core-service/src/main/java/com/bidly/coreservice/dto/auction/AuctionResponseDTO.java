package com.bidly.coreservice.dto.auction;

import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.AuctionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuctionResponseDTO {
    private UUID id;
    private ProductPublicDTO product;

    private UserPublicDTO seller;
    private UserPublicDTO winner;

    private AuctionStatus status;

    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal minIncrement;

    private Instant startsAt;
    private Instant endsAt;

    private long bidCount;
    private boolean deleted;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}