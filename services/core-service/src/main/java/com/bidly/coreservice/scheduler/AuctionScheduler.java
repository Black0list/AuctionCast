package com.bidly.coreservice.scheduler;

import com.bidly.coreservice.service.AuctionService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class AuctionScheduler {

    private final AuctionService auctionService;

    @PostConstruct
    void init() {
        log.info("AuctionScheduler bean created");
    }

    @Scheduled(fixedDelayString = "${auction.scheduler.activate-delay-ms:10000}")
    public void activateScheduled() {
        log.info("AuctionScheduler.activateScheduled tick");
        auctionService.activateScheduledAuctions();
    }

    @Scheduled(fixedDelayString = "${auction.scheduler.end-delay-ms:10000}")
    public void endExpired() {
        log.info("AuctionScheduler.endExpired tick");
        auctionService.endExpiredAuctions();
    }
}