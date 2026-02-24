package com.bidly.coreservice.dto.auction;

import com.bidly.common.enums.AuctionStatus;
import java.util.UUID;

public record AuctionActionResponse(
        UUID auctionId,
        AuctionStatus status
) {}