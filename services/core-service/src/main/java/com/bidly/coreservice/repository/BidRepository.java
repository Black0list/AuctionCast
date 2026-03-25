package com.bidly.coreservice.repository;

import com.bidly.coreservice.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByAuction_IdOrderByCreatedAtDesc(UUID auctionId);
    void deleteByBidderId(String bidderId);
}