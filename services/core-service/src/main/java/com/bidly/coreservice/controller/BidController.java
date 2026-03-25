package com.bidly.coreservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.coreservice.dto.bid.BidResponseDTO;
import com.bidly.coreservice.dto.bid.PlaceBidDTO;
import com.bidly.coreservice.service.BidService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;


@AllArgsConstructor
@RestController
@RequestMapping("/bids")
public class BidController {

    private final BidService bidService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<BidResponseDTO> placeBid(
            @Valid @RequestBody PlaceBidDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        return bidService.placeBid(dto, userId);
    }
}