package com.bidly.coreservice.repository;

import com.bidly.common.enums.AuctionStatus;
import com.bidly.coreservice.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {

    List<Auction> findByStatusAndDeletedFalse(AuctionStatus status);

    List<Auction> findBySellerIdAndDeletedFalse(String sellerId);

    Optional<Auction> findByProductId(UUID productId);

    List<Auction> findByStatusAndStartsAtLessThanEqualAndDeletedFalse(AuctionStatus status, Instant now);

    List<Auction> findByStatusAndEndsAtLessThanEqualAndDeletedFalse(AuctionStatus status, Instant now);

    long countByStatusAndDeletedFalse(AuctionStatus status);

    void deleteBySellerId(String sellerId);
}