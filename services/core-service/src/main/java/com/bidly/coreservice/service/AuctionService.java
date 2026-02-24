package com.bidly.coreservice.service;

import com.bidly.coreservice.repository.AuctionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
}
