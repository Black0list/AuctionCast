package com.bidly.coreservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.AuctionStatus;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.coreservice.client.ProductClient;
import com.bidly.coreservice.client.UserClient;
import com.bidly.coreservice.dto.auction.AuctionResponseDTO;
import com.bidly.coreservice.dto.auction.CreateAuctionDTO;
import com.bidly.coreservice.dto.auction.ScheduleAuctionDTO;
import com.bidly.coreservice.dto.auction.UpdateAuctionDTO;
import com.bidly.coreservice.entity.Auction;
import com.bidly.coreservice.mapper.AuctionMapper;
import com.bidly.coreservice.repository.AuctionRepository;
import com.bidly.coreservice.util.Util;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    @Transactional
    public ApiResponse<AuctionResponseDTO> create(CreateAuctionDTO dto, String sellerId) {
        assertSeller(sellerId);
        assertProductOwner(dto.getProductId(), sellerId);
        assertNoOtherAuctionForProduct(dto.getProductId());

        validateTimes(dto.getStartsAt(), dto.getEndsAt());

        Auction auction = AuctionMapper.toEntity(dto, sellerId);
        auction.setDeleted(false);
        auction.setBidCount(0);
        auction.setCurrentPrice(dto.getStartPrice());
        auction.setCurrentWinnerId(null);

        Instant now = Instant.now();
        if (dto.getStartsAt() != null && dto.getStartsAt().isAfter(now)) {
            auction.setStatus(AuctionStatus.SCHEDULED);
        } else {
            auction.setStatus(AuctionStatus.ACTIVE);
            auction.setStartsAt(now);
        }

        auctionRepository.save(auction);

        UserPublicDTO sellerDto = Util.getUserDto();
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, null, productDto),
                "Auction created successfully"
        );
    }

    @Transactional
    public ApiResponse<AuctionResponseDTO> update(UUID auctionId, UpdateAuctionDTO dto, String sellerId) {
        assertSeller(sellerId);

        Auction auction = getOwnedAuction(auctionId, sellerId);

        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT auctions can be updated");
        }

        validateTimes(dto.getStartsAt(), dto.getEndsAt());

        auction.setStartPrice(dto.getStartPrice());
        auction.setMinIncrement(dto.getMinIncrement());
        auction.setStartsAt(dto.getStartsAt());
        auction.setEndsAt(dto.getEndsAt());

        auction.setCurrentPrice(dto.getStartPrice());
        auction.setCurrentWinnerId(null);
        auction.setBidCount(0);

        auctionRepository.save(auction);

        UserPublicDTO sellerDto = Util.getUserDto();
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, null, productDto),
                "Auction updated successfully"
        );
    }

    @Transactional
    public ApiResponse<AuctionResponseDTO> schedule(UUID auctionId, ScheduleAuctionDTO dto, String sellerId) {
        assertSeller(sellerId);

        Auction auction = getOwnedAuction(auctionId, sellerId);

        if (auction.getStatus() != AuctionStatus.DRAFT) {
            throw new IllegalArgumentException("Only DRAFT auctions can be scheduled");
        }

        validateTimes(dto.getStartsAt(), dto.getEndsAt());

        if (!dto.getStartsAt().isAfter(Instant.now())) {
            throw new IllegalArgumentException("startsAt must be in the future");
        }

        auction.setStartsAt(dto.getStartsAt());
        auction.setEndsAt(dto.getEndsAt());
        auction.setStatus(AuctionStatus.SCHEDULED);

        auctionRepository.save(auction);

        UserPublicDTO sellerDto = Util.getUserDto();
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, null, productDto),
                "Auction scheduled successfully"
        );
    }

    @Transactional
    public ApiResponse<AuctionResponseDTO> publishNow(UUID auctionId, String sellerId) {
        assertSeller(sellerId);

        Auction auction = getOwnedAuction(auctionId, sellerId);

        if (auction.getStatus() != AuctionStatus.DRAFT && auction.getStatus() != AuctionStatus.SCHEDULED) {
            throw new IllegalArgumentException("Only DRAFT or SCHEDULED auctions can be published");
        }

        Instant now = Instant.now();

        if (auction.getEndsAt() == null) {
            throw new IllegalArgumentException("Auction endsAt is not set");
        }

        if (!auction.getEndsAt().isAfter(now)) {
            throw new IllegalArgumentException("endsAt must be in the future");
        }

        auction.setStartsAt(now);
        auction.setStatus(AuctionStatus.ACTIVE);

        if (auction.getCurrentPrice() == null) {
            auction.setCurrentPrice(auction.getStartPrice());
        }

        auctionRepository.save(auction);

        UserPublicDTO sellerDto = Util.getUserDto();
        UserPublicDTO winnerDto = auction.getCurrentWinnerId() == null ? null : resolveUserPublic(auction.getCurrentWinnerId(), sellerId, sellerDto);
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, winnerDto, productDto),
                "Auction published successfully"
        );
    }

    @Transactional
    public ApiResponse<AuctionResponseDTO> cancel(UUID auctionId, String sellerId) {
        assertSeller(sellerId);

        Auction auction = getOwnedAuction(auctionId, sellerId);

        if (auction.getStatus() == AuctionStatus.ENDED || auction.getStatus() == AuctionStatus.CANCELLED) {
            throw new IllegalArgumentException("Auction cannot be cancelled");
        }

        if (auction.getStatus() == AuctionStatus.ACTIVE && auction.getBidCount() > 0) {
            throw new IllegalArgumentException("Auction cannot be cancelled after bids were placed");
        }

        auction.setStatus(AuctionStatus.CANCELLED);
        auctionRepository.save(auction);

        UserPublicDTO sellerDto = Util.getUserDto();
        UserPublicDTO winnerDto = auction.getCurrentWinnerId() == null ? null : resolveUserPublic(auction.getCurrentWinnerId(), sellerId, sellerDto);
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, winnerDto, productDto),
                "Auction cancelled successfully"
        );
    }

    @Transactional
    public ApiResponse<AuctionResponseDTO> end(UUID auctionId, String sellerId) {
        assertSeller(sellerId);

        Auction auction = getOwnedAuction(auctionId, sellerId);

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalArgumentException("Only ACTIVE auctions can be ended");
        }

        auction.setStatus(AuctionStatus.ENDED);
        auction.setEndsAt(Instant.now());
        auctionRepository.save(auction);

        UserPublicDTO sellerDto = Util.getUserDto();
        UserPublicDTO winnerDto = auction.getCurrentWinnerId() == null ? null : resolveUserPublic(auction.getCurrentWinnerId(), sellerId, sellerDto);
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, winnerDto, productDto),
                "Auction ended successfully"
        );
    }

    public ApiResponse<AuctionResponseDTO> get(UUID auctionId, String requesterId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.isDeleted()) {
            throw new ResourceNotFoundException("Auction not found");
        }

        UserPublicDTO requesterDto = Util.getUserDto();

        UserPublicDTO sellerDto = resolveUserPublic(auction.getSellerId(), requesterId, requesterDto);
        UserPublicDTO winnerDto = auction.getCurrentWinnerId() == null ? null : resolveUserPublic(auction.getCurrentWinnerId(), requesterId, requesterDto);
        ProductPublicDTO productDto = productClient.findProduct(auction.getProductId()).getData();

        return ApiResponse.success(
                AuctionMapper.toResponseDto(auction, sellerDto, winnerDto, productDto),
                "Auction retrieved successfully"
        );
    }

    public ApiResponse<List<AuctionResponseDTO>> listActive(String requesterId) {
        List<Auction> auctions = auctionRepository.findByStatusAndDeletedFalse(AuctionStatus.ACTIVE);

        if (auctions.isEmpty()) {
            throw new ResourceNotFoundException("No active auctions found");
        }

        UserPublicDTO requesterDto = Util.getUserDto();

        List<AuctionResponseDTO> dtos = auctions.stream()
                .map(a -> {
                    UserPublicDTO sellerDto = resolveUserPublic(a.getSellerId(), requesterId, requesterDto);
                    UserPublicDTO winnerDto = a.getCurrentWinnerId() == null ? null : resolveUserPublic(a.getCurrentWinnerId(), requesterId, requesterDto);
                    ProductPublicDTO productDto = productClient.findProduct(a.getProductId()).getData();
                    return AuctionMapper.toResponseDto(a, sellerDto, winnerDto, productDto);
                })
                .toList();

        return ApiResponse.success(dtos, "Active auctions retrieved successfully");
    }

    public ApiResponse<List<AuctionResponseDTO>> listMyAuctions(String sellerId) {
        assertSeller(sellerId);

        List<Auction> auctions = auctionRepository.findBySellerIdAndDeletedFalse(sellerId);

        if (auctions.isEmpty()) {
            throw new ResourceNotFoundException("No auctions found");
        }

        UserPublicDTO sellerDto = Util.getUserDto();

        List<AuctionResponseDTO> dtos = auctions.stream()
                .map(a -> {
                    UserPublicDTO winnerDto = a.getCurrentWinnerId() == null ? null : resolveUserPublic(a.getCurrentWinnerId(), sellerId, sellerDto);
                    ProductPublicDTO productDto = productClient.findProduct(a.getProductId()).getData();
                    return AuctionMapper.toResponseDto(a, sellerDto, winnerDto, productDto);
                })
                .toList();

        return ApiResponse.success(dtos, "My auctions retrieved successfully");
    }

    @Transactional
    public void activateScheduledAuctions() {
        Instant now = Instant.now();
        List<Auction> auctions = auctionRepository.findByStatusAndStartsAtLessThanEqualAndDeletedFalse(AuctionStatus.SCHEDULED, now);

        for (Auction auction : auctions) {
            if (auction.getEndsAt() != null) {
                if (auction.getEndsAt().isAfter(now)) {
                    auction.setStatus(AuctionStatus.ACTIVE);
                    if (auction.getStartsAt() == null) {
                        auction.setStartsAt(now);
                    }
                    if (auction.getCurrentPrice() == null) {
                        auction.setCurrentPrice(auction.getStartPrice());
                    }
                } else {
                   auction.setStatus(AuctionStatus.ENDED);
                }
                auctionRepository.save(auction);
            }
        }
    }

    @Transactional
    public void endExpiredAuctions() {
        Instant now = Instant.now();
        List<Auction> auctions = auctionRepository.findByStatusAndEndsAtLessThanEqualAndDeletedFalse(AuctionStatus.ACTIVE, now);

        for (Auction auction : auctions) {
            auction.setStatus(AuctionStatus.ENDED);
            auctionRepository.save(auction);
        }
    }

    private void validateTimes(Instant startsAt, Instant endsAt) {
        if (endsAt == null) {
            throw new IllegalArgumentException("endsAt is required");
        }
        if (startsAt != null && !endsAt.isAfter(startsAt)) {
            throw new IllegalArgumentException("endsAt must be after startsAt");
        }
        if (!endsAt.isAfter(Instant.now())) {
            throw new IllegalArgumentException("endsAt must be in the future");
        }
    }

    private Auction getOwnedAuction(UUID auctionId, String sellerId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.isDeleted()) {
            throw new ResourceNotFoundException("Auction not found");
        }

        if (!auction.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("You are not allowed to manage this auction");
        }

        return auction;
    }

    private void assertSeller(String userId) {
        ApiResponse<Boolean> res = userClient.isSeller(userId);

        if (res == null || !res.isSuccess() || res.getData() == null) {
            throw new IllegalArgumentException("Could not verify seller status");
        }

        if (!Boolean.TRUE.equals(res.getData())) {
            throw new AccessDeniedException("User is not a verified seller. Please apply to become a seller first.");
        }
    }

    private void assertProductOwner(UUID productId, String userId) {
        ApiResponse<Boolean> res;

        try {
            res = productClient.isProductOwner(productId, userId);
        } catch (FeignException.NotFound ex) {
            throw new IllegalArgumentException("Product not found");
        } catch (FeignException ex) {
            throw new IllegalArgumentException("Product service not available");
        }

        if (res == null || res.getData() == null) {
            throw new IllegalArgumentException("Product service not available");
        }

        if (!Boolean.TRUE.equals(res.getData())) {
            throw new AccessDeniedException("You are not allowed to create an auction for this product");
        }
    }

    private void assertNoOtherAuctionForProduct(UUID productId) {
        if (auctionRepository.findByProductId(productId).isPresent()) {
            throw new IllegalArgumentException("Auction already exists for this product");
        }
    }

    private UserPublicDTO resolveUserPublic(String userId, String requesterId, UserPublicDTO requesterDto) {
        if (userId == null) return null;
        if (userId.equals(requesterId)) return requesterDto;

        ApiResponse<UserPublicDTO> res = userClient.findOne(userId);

        if (res == null || !res.isSuccess() || res.getData() == null) {
            throw new ResourceNotFoundException("User not found");
        }

        return res.getData();
    }
}