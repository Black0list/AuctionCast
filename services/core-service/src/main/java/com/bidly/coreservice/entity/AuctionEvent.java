package com.bidly.coreservice.entity;

import com.bidly.common.enums.AuctionEventType;
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
public class AuctionEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID auctionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionEventType type;

    @Column(nullable = false)
    private String actorId; // seller/bidder/system

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}