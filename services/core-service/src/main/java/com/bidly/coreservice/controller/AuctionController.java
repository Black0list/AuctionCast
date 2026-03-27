package com.bidly.coreservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.coreservice.dto.DashboardStatsDTO;
import com.bidly.coreservice.dto.auction.AuctionResponseDTO;
import com.bidly.coreservice.dto.auction.CreateAuctionDTO;
import com.bidly.coreservice.dto.auction.ScheduleAuctionDTO;
import com.bidly.coreservice.dto.auction.UpdateAuctionDTO;
import com.bidly.coreservice.service.AuctionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/auctions")
public class AuctionController {

    private final AuctionService auctionService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<AuctionResponseDTO> create(
            @Valid @RequestBody CreateAuctionDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return auctionService.create(dto, jwt.getSubject());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<AuctionResponseDTO> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateAuctionDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return auctionService.update(id, dto, jwt.getSubject());
    }

    @PostMapping("/{id}/schedule")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<AuctionResponseDTO> schedule(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ScheduleAuctionDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return auctionService.schedule(id, dto, jwt.getSubject());
    }

    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<AuctionResponseDTO> publish(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return auctionService.publishNow(id, jwt.getSubject());
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<AuctionResponseDTO> cancel(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return auctionService.cancel(id, jwt.getSubject());
    }

    @PostMapping("/{id}/end")
    @PreAuthorize("hasRole('SELLER')")
    public ApiResponse<AuctionResponseDTO> end(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return auctionService.end(id, jwt.getSubject());
    }

    @GetMapping("/{id}")
    public ApiResponse<AuctionResponseDTO> get(
            @PathVariable("id") UUID id,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt != null ? jwt.getSubject() : null;
        return auctionService.get(id, userId);
    }

    @GetMapping("/active")
    public ApiResponse<List<AuctionResponseDTO>> listActive(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt != null ? jwt.getSubject() : null;
        return auctionService.listActive(userId);
    }

    @GetMapping("/me")
    public ApiResponse<List<AuctionResponseDTO>> myAuctions(@AuthenticationPrincipal Jwt jwt) {
        return auctionService.listMyAuctions(jwt.getSubject());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<AuctionResponseDTO>> listAllAdmin() {
        return auctionService.listAll();
    }

    @GetMapping("/admin/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DashboardStatsDTO> getStats() {
        return auctionService.getDashboardStats();
    }
}