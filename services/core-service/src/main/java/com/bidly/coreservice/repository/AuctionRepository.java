package com.bidly.coreservice.repository;

import com.bidly.coreservice.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuctionRepository extends JpaRepository<Auction, UUID> {
}
