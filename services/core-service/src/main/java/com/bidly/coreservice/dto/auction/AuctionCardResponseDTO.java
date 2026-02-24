package com.bidly.coreservice.dto.auction;


import com.bidly.common.enums.AuctionStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class AuctionCardResponseDTO {
    private UUID id;
    private UUID productId;
    private AuctionStatus status;
    private BigDecimal currentPrice;
    private LocalDateTime endsAt;
}