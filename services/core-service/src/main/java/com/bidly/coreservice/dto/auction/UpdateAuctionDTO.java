package com.bidly.coreservice.dto.auction;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
public class UpdateAuctionDTO {

    @NotNull
    @DecimalMin(value = "0.01", message = "Start price must be greater than 0")
    private BigDecimal startPrice;

    @NotNull
    @DecimalMin(value = "0.01", message = "Min increment must be greater than 0")
    private BigDecimal minIncrement;

    private Instant startsAt;

    @NotNull
    private Instant endsAt;
}