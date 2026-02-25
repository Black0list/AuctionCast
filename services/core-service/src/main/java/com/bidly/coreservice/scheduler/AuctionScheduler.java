package com.bidly.coreservice.scheduler;

import com.bidly.coreservice.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class AuctionScheduler {

    private final AuctionService auctionService;

    @Scheduled(fixedDelayString = "${auction.scheduler.activate-delay-ms:10000}")
    public void activateScheduled() {
        auctionService.activateScheduledAuctions();
    }

    @Scheduled(fixedDelayString = "${auction.scheduler.end-delay-ms:10000}")
    public void endExpired() {
        auctionService.endExpiredAuctions();
    }
}