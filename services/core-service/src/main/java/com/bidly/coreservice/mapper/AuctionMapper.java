package com.bidly.coreservice.mapper;

import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.coreservice.dto.auction.AuctionResponseDTO;
import com.bidly.coreservice.dto.auction.CreateAuctionDTO;
import com.bidly.coreservice.entity.Auction;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AuctionMapper {

    public static AuctionResponseDTO toResponseDto(Auction auction, UserPublicDTO sellerDto, UserPublicDTO winnerDto, ProductPublicDTO product) {
        return AuctionResponseDTO.builder()
                .id(auction.getId())
                .product(product)
                .seller(sellerDto)
                .winner(winnerDto)
                .status(auction.getStatus())
                .startPrice(auction.getStartPrice())
                .currentPrice(auction.getCurrentPrice())
                .minIncrement(auction.getMinIncrement())
                .startsAt(auction.getStartsAt())
                .endsAt(auction.getEndsAt())
                .bidCount(auction.getBidCount())
                .deleted(auction.isDeleted())
                .createdAt(auction.getCreatedAt())
                .updatedAt(auction.getUpdatedAt())
                .build();
    }

    public static Auction toEntity(CreateAuctionDTO dto, String sellerId) {
        return Auction.builder()
                .productId(dto.getProductId())
                .sellerId(sellerId)
                .startPrice(dto.getStartPrice())
                .currentPrice(dto.getStartPrice())
                .minIncrement(dto.getMinIncrement() == null ? BigDecimal.ONE : dto.getMinIncrement())
                .startsAt(dto.getStartsAt())
                .endsAt(dto.getEndsAt())
                .build();
    }
}