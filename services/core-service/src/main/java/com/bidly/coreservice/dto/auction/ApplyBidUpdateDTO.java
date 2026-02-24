package com.bidly.coreservice.dto.auction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ApplyBidUpdateDTO {
    private String bidderId;
    private BigDecimal newPrice;

}