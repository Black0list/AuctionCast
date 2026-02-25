package com.bidly.coreservice.dto.bid;

import com.bidly.common.dto.UserPublicDTO;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class BidResponseDTO {
    private UUID id;
    private UUID auctionId;
    private UserPublicDTO bidder;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}