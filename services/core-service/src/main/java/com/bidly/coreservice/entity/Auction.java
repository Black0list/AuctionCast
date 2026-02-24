package com.bidly.coreservice.entity;

import com.bidly.common.enums.AuctionStatus;
import com.bidly.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "auctions",
        uniqueConstraints = @UniqueConstraint(name = "uk_auction_product", columnNames = "productId"),
        indexes = {
                @Index(name = "idx_auction_status", columnList = "status"),
                @Index(name = "idx_auction_seller", columnList = "sellerId"),
                @Index(name = "idx_auction_product", columnList = "productId")
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Auction extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID productId;

    @Column(nullable = false)
    private String sellerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal startPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentPrice;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal minIncrement;

    private String currentWinnerId;

    private LocalDateTime startsAt;
    private LocalDateTime endsAt;

    @Version
    private Long version;

    private boolean deleted;
}