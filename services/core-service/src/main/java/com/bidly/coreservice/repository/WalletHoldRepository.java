package com.bidly.coreservice.repository;

import com.bidly.coreservice.entity.WalletHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletHoldRepository extends JpaRepository<WalletHold, UUID> {

    Optional<WalletHold> findByAuctionIdAndUserId(UUID auctionId, String userId);

    long deleteByAuctionIdAndUserId(UUID auctionId, String userId);

    void deleteByUserId(String userId);
}