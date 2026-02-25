package com.bidly.coreservice.entity;

import com.bidly.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wallets")
public class Wallet extends BaseEntity {

    @Id
    @Column(name = "user_id", length = 128, nullable = false, updatable = false)
    private String userId;

    @Column(name = "available_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal availableBalance;

    @Column(name = "reserved_balance", nullable = false, precision = 19, scale = 2)
    private BigDecimal reservedBalance;

    @Version
    @Column(nullable = false)
    private long version;

    @PrePersist
    void prePersist() {
        if (availableBalance == null) availableBalance = BigDecimal.ZERO;
        if (reservedBalance == null) reservedBalance = BigDecimal.ZERO;
    }
}