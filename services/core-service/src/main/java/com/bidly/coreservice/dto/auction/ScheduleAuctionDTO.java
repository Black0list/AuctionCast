package com.bidly.coreservice.dto.auction;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class ScheduleAuctionDTO {

    @NotNull
    private Instant startsAt;

    @NotNull
    private Instant endsAt;
}