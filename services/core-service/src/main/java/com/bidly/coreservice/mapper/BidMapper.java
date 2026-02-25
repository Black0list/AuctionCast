package com.bidly.coreservice.mapper;

import com.bidly.common.dto.UserPublicDTO;
import com.bidly.coreservice.dto.bid.BidResponseDTO;
import com.bidly.coreservice.entity.Bid;
import org.springframework.stereotype.Component;

@Component
public class BidMapper {

    public static BidResponseDTO toResponseDto(Bid bid, UserPublicDTO bidder) {
        return BidResponseDTO.builder()
                .id(bid.getId())
                .auctionId(bid.getAuction().getId())
                .bidder(bidder)
                .amount(bid.getAmount())
                .createdAt(bid.getCreatedAt())
                .build();
    }
}