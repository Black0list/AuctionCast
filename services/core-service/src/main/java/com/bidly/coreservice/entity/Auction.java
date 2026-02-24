package com.bidly.coreservice.entity;

import com.bidly.common.enums.AuctionStatus;
import com.bidly.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "auctions",
        indexes = {
                @Index(name = "idx_auction_status", columnList = "status"),
                @Index(name = "idx_auction_seller", columnList = "seller_id"),
                @Index(name = "idx_auction_product", columnList = "product_id"),
                @Index(name = "idx_auction_ends_at", columnList = "ends_at")
        }
)
public class Auction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "seller_id", nullable = false, updatable = false)
    private UUID sellerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(name = "start_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal startPrice;

    @Column(name = "current_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;

    @Column(name = "min_increment", nullable = false, precision = 19, scale = 2)
    private BigDecimal minIncrement;

    @Column(name = "current_winner_id")
    private UUID currentWinnerId;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(name = "bid_count", nullable = false)
    private long bidCount;

    @Version
    @Column(nullable = false)
    private long version;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToMany(mappedBy = "auction", fetch = FetchType.LAZY)
    private List<AuctionEvent> events = new ArrayList<>();

    @PrePersist
    void prePersist() {
        if (currentPrice == null) currentPrice = startPrice;
        if (status == null) status = AuctionStatus.DRAFT;
        if (minIncrement == null) minIncrement = BigDecimal.ONE; // choose your default
    }
}