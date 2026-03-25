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
public class AuctionProcessor {

    private final AuctionRepository auctionRepository;
    private final ProductClient productClient;
    private final OrderService orderService;
    private final WalletReservationService walletReservationService;

    @Transactional
    public void processScheduledAuction(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        Instant now = Instant.now();
        if (auction.getEndsAt() != null) {
            if (auction.getEndsAt().isAfter(now)) {
                auction.setStatus(AuctionStatus.ACTIVE);
                if (auction.getStartsAt() == null) {
                    auction.setStartsAt(now);
                }
                if (auction.getCurrentPrice() == null) {
                    auction.setCurrentPrice(auction.getStartPrice());
                }
                productClient.updateStatus(auction.getProductId(), ProductStatus.IN_AUCTION);
            } else {
                auction.setStatus(AuctionStatus.ENDED);
                if (auction.getCurrentWinnerId() != null) {
                    UserPublicDTO winner = orderService.resolveUserStrict(auction.getCurrentWinnerId());
                    orderService.automatedCreateOrder(auction, winner);
                    walletReservationService.chargeReservedFunds(winner.getId(), auction.getCurrentPrice());
                    productClient.updateStatus(auction.getProductId(), ProductStatus.SOLD);
                } else {
                    productClient.updateStatus(auction.getProductId(), ProductStatus.UNSOLD);
                }
            }
            auctionRepository.save(auction);
        }
    }

    @Transactional
    public void processEndAuction(UUID auctionId) {
        Auction auction = auctionRepository.findById(auctionId).orElseThrow();
        if (auction.getCurrentWinnerId() != null) {
            UserPublicDTO winner = orderService.resolveUserStrict(auction.getCurrentWinnerId());
            orderService.automatedCreateOrder(auction, winner);
            walletReservationService.chargeReservedFunds(winner.getId(), auction.getCurrentPrice());
            productClient.updateStatus(auction.getProductId(), ProductStatus.SOLD);
        } else {
            productClient.updateStatus(auction.getProductId(), ProductStatus.UNSOLD);
        }
        auction.setStatus(AuctionStatus.ENDED);
        auctionRepository.save(auction);
    }
}
