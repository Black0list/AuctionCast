package com.bidly.coreservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.enums.AuctionStatus;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.coreservice.dto.bid.BidResponseDTO;
import com.bidly.coreservice.dto.bid.PlaceBidDTO;
import com.bidly.coreservice.entity.Auction;
import com.bidly.coreservice.entity.Bid;
import com.bidly.coreservice.mapper.BidMapper;
import com.bidly.coreservice.repository.AuctionRepository;
import com.bidly.coreservice.repository.BidRepository;
import com.bidly.coreservice.util.Util;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@AllArgsConstructor
@Service
public class BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final WalletReservationService walletReservationService;

    @Transactional
    public ApiResponse<BidResponseDTO> placeBid(PlaceBidDTO dto, String userId) {
        Auction auction = auctionRepository.findById(dto.getAuctionId())
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.isDeleted()) {
            throw new IllegalArgumentException("Auction is deleted");
        }

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalArgumentException("Auction is not active");
        }

        if (auction.getEndsAt() != null && Instant.now().isAfter(auction.getEndsAt())) {
            throw new IllegalArgumentException("Auction already ended");
        }

        BigDecimal amount = dto.getAmount();
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException("Bid amount must be greater than 0");
        }

        BigDecimal minAllowed = auction.getCurrentPrice().add(auction.getMinIncrement());
        if (amount.compareTo(minAllowed) < 0) {
            throw new IllegalArgumentException("Bid must be at least " + minAllowed);
        }

        String prevWinner = auction.getCurrentWinnerId();

        walletReservationService.reserveForLeader(userId, auction, amount);

        if (prevWinner != null && !prevWinner.equals(userId)) {
            walletReservationService.releaseForOutbid(prevWinner, auction);
        }

        Bid bid = Bid.builder()
                .auction(auction)
                .bidderId(userId)
                .amount(amount)
                .build();

        bidRepository.save(bid);

        auction.setCurrentPrice(amount);
        auction.setCurrentWinnerId(userId);
        auction.setBidCount(auction.getBidCount() + 1);
        auctionRepository.save(auction);

        return ApiResponse.success(BidMapper.toResponseDto(bid, Util.getUserDto()), "Bid placed successfully");
    }
}