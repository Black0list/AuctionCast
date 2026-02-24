package com.bidly.coreservice.controller;


import com.bidly.coreservice.service.AuctionService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;
}