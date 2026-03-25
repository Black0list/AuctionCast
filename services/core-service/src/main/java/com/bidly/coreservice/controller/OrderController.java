package com.bidly.coreservice.controller;

import com.bidly.common.dto.ApiResponse;
import com.bidly.coreservice.dto.order.CreateOrderDTO;
import com.bidly.coreservice.dto.order.OrderPublicResponseDTO;
import com.bidly.coreservice.dto.order.OrderResponseDTO;
import com.bidly.coreservice.dto.order.ShipOrderDTO;
import com.bidly.coreservice.service.OrderService;
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
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/auction/{auctionId}")
    public ApiResponse<OrderResponseDTO> createForAuction(
            @PathVariable("auctionId") UUID auctionId,
            @Valid @RequestBody CreateOrderDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return orderService.createForAuction(auctionId, dto, jwt.getSubject());
    }

    @PostMapping("/{orderId}/ship")
    public ApiResponse<OrderResponseDTO> ship(
            @PathVariable("orderId") UUID orderId,
            @Valid @RequestBody ShipOrderDTO dto,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return orderService.ship(orderId, dto, jwt.getSubject());
    }

    @PostMapping("/{orderId}/deliver")
    public ApiResponse<OrderResponseDTO> deliver(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return orderService.confirmDelivery(orderId, jwt.getSubject());
    }

    @GetMapping("/me/purchases")
    public ApiResponse<List<OrderPublicResponseDTO>> myPurchases(@AuthenticationPrincipal Jwt jwt) {
        return orderService.myPurchases(jwt.getSubject());
    }

    @GetMapping("/me/sales")
    public ApiResponse<List<OrderPublicResponseDTO>> mySales(@AuthenticationPrincipal Jwt jwt) {
        return orderService.mySales(jwt.getSubject());
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderPublicResponseDTO> get(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        return orderService.get(orderId, jwt.getSubject());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<OrderPublicResponseDTO>> listAllAdmin() {
        return orderService.listAll();
    }
}