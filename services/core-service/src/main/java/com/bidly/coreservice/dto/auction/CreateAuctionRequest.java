package com.bidly.coreservice.dto.auction;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateAuctionRequest(
        UUID productId,
        BigDecimal startPrice,
        BigDecimal minIncrement,
        Instant startsAt,
        Instant endsAt
) {}