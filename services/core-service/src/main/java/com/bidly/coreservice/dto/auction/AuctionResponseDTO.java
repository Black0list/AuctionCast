package com.bidly.coreservice.dto.auction;

import com.bidly.common.enums.AuctionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuctionResponseDTO {
    private UUID id;
    private UUID productId;
    private String sellerId;
    private AuctionStatus status;
    private BigDecimal startPrice;
    private BigDecimal currentPrice;
    private BigDecimal minIncrement;
    private String currentWinnerId;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}