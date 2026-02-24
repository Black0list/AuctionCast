package com.bidly.coreservice.dto.auction;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class AuctionDTO {
    private UUID productId;
    private BigDecimal startPrice;
    private BigDecimal minIncrement;
}