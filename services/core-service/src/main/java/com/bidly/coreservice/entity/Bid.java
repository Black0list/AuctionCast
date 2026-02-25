package com.bidly.coreservice.entity;

import com.bidly.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "bids",
        indexes = {
                @Index(name = "idx_bid_auction_created", columnList = "auction_id, created_at"),
                @Index(name = "idx_bid_bidder", columnList = "bidder_id")
        }
)
public class Bid extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Column(name = "bidder_id", nullable = false)
    private String bidderId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
}