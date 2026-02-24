package com.bidly.coreservice.dto.auction;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScheduleAuctionDTO {
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}