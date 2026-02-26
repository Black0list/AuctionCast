package com.bidly.coreservice.repository;

import com.bidly.coreservice.entity.Order;
import com.bidly.common.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findByAuctionId(UUID auctionId);
    List<Order> findByBuyerIdOrderByCreatedAtDesc(String buyerId);
    List<Order> findBySellerIdOrderByCreatedAtDesc(String sellerId);
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
}