package com.bidly.coreservice.dto.auction;

import com.bidly.common.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionDetailsResponse(
        UUID id,
        UUID productId,
        UUID sellerId,
        AuctionStatus status,
        BigDecimal startPrice,
        BigDecimal currentPrice,
        BigDecimal minIncrement,
        UUID currentWinnerId,
        Instant startsAt,
        Instant endsAt,
        long bidCount
) {}
