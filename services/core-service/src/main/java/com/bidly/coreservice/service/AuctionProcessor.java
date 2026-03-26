package com.bidly.coreservice.service;

import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.AuctionStatus;
import com.bidly.common.enums.ProductStatus;
import com.bidly.coreservice.client.ProductClient;
import com.bidly.coreservice.entity.Auction;
import com.bidly.coreservice.repository.AuctionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@Service
@Slf4j
public class AuctionProcessor {

    private final AuctionRepository auctionRepository;
    private final ProductClient productClient;
    private final OrderService orderService;
    private final WalletReservationService walletReservationService;

    @Transactional
    public void processScheduledAuction(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        if (auction.getStatus() != AuctionStatus.SCHEDULED) {
            return;
        }

        Instant now = Instant.now();
        if (auction.getEndsAt() != null) {
            if (auction.getEndsAt().isAfter(now)) {
                log.info("Activating auction {}", auctionId);
                auction.setStatus(AuctionStatus.ACTIVE);
                if (auction.getStartsAt() == null) {
                    auction.setStartsAt(now);
                }
                if (auction.getCurrentPrice() == null) {
                    auction.setCurrentPrice(auction.getStartPrice());
                }
                try {
                    productClient.updateStatus(auction.getProductId(), ProductStatus.IN_AUCTION);
                } catch (Exception e) {
                    log.error("Failed to update product status for auction {}: {}", auctionId, e.getMessage());
                }
            } else {
                log.info("Auction {} expired before activation, ending now", auctionId);
                finalizeAuction(auction);
            }
            auctionRepository.save(auction);
        }
    }

    @Transactional
    public void processEndAuction(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        if (auction.getStatus() == AuctionStatus.ENDED) {
            return;
        }

        log.info("Ending auction {}", auctionId);
        finalizeAuction(auction);
        auctionRepository.save(auction);
    }

    private void finalizeAuction(Auction auction) {
        auction.setStatus(AuctionStatus.ENDED);
        
        if (auction.getCurrentWinnerId() != null) {
            try {
                UserPublicDTO winner = orderService.resolveUserStrict(auction.getCurrentWinnerId());
                orderService.automatedCreateOrder(auction, winner);
                walletReservationService.chargeReservedFunds(winner.getId(), auction.getCurrentPrice());
                productClient.updateStatus(auction.getProductId(), ProductStatus.SOLD);
            } catch (Exception e) {
                log.error("Fulfillment failed for auction {}: {}. Auction is marked as ENDED but requires manual intervention.", 
                        auction.getId(), e.getMessage());
            }
        } else {
            try {
                productClient.updateStatus(auction.getProductId(), ProductStatus.UNSOLD);
            } catch (Exception e) {
                log.error("Failed to update product status to UNSOLD for auction {}: {}", auction.getId(), e.getMessage());
            }
        }
    }
}
