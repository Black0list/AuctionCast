package com.bidly.coreservice.entity;

import com.bidly.common.enums.AuctionEventType;
import com.bidly.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "auction_events", indexes = {
        @Index(name = "idx_auction_events_auction", columnList = "auctionId")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "auction_id", nullable = false)
    private Auction auction;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionEventType type;

    @Column(nullable = false)
    private String actorId; // seller/bidder/system

    @Column(columnDefinition = "TEXT")
    private String note;
}