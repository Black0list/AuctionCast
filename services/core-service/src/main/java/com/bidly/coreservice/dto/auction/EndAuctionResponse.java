package com.bidly.coreservice.dto.auction;

import java.math.BigDecimal;
import java.util.UUID;

public record EndAuctionResponse(
        UUID auctionId,
        UUID winnerUserId,
        BigDecimal finalPrice,
        long bidCount
) {}
