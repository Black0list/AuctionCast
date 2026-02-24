package com.bidly.coreservice.entity;

import com.bidly.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "wallet_holds",
        uniqueConstraints = @UniqueConstraint(name = "uk_wallet_hold_auction_user", columnNames = {"auction_id", "user_id"}),
        indexes = {
                @Index(name = "idx_wallet_holds_user", columnList = "user_id"),
                @Index(name = "idx_wallet_holds_auction", columnList = "auction_id")
        }
)
public class WalletHold extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "auction_id", nullable = false)
    private UUID auctionId;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Version
    @Column(nullable = false)
    private long version;

    @PrePersist
    void prePersist() {
        if (amount == null) amount = BigDecimal.ZERO;
    }
}