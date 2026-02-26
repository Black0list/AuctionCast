package com.bidly.coreservice.service;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.AuctionStatus;
import com.bidly.common.exception.ResourceNotFoundException;
import com.bidly.coreservice.client.ProductClient;
import com.bidly.coreservice.client.UserClient;
import com.bidly.coreservice.dto.order.CreateOrderDTO;
import com.bidly.coreservice.dto.order.OrderPublicResponseDTO;
import com.bidly.coreservice.dto.order.OrderResponseDTO;
import com.bidly.coreservice.dto.order.ShipOrderDTO;
import com.bidly.coreservice.entity.Auction;
import com.bidly.coreservice.entity.Order;
import com.bidly.common.enums.OrderStatus;
import com.bidly.coreservice.mapper.OrderMapper;
import com.bidly.coreservice.repository.AuctionRepository;
import com.bidly.coreservice.repository.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class OrderService {

    private final AuctionRepository auctionRepository;
    private final OrderRepository orderRepository;
    private final UserClient userClient;
    private final ProductClient productClient;

    @Transactional
    public ApiResponse<OrderResponseDTO> createForAuction(UUID auctionId, CreateOrderDTO dto, String buyerId) {
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException("Auction not found"));

        if (auction.isDeleted()) {
            throw new ResourceNotFoundException("Auction not found");
        }

        if (auction.getStatus() != AuctionStatus.ENDED) {
            throw new IllegalArgumentException("Order can be created only when auction is ENDED");
        }

        if (auction.getCurrentWinnerId() == null) {
            throw new IllegalArgumentException("Auction has no winner");
        }

        if (!auction.getCurrentWinnerId().equals(buyerId)) {
            throw new AccessDeniedException("Only the auction winner can create the order");
        }

        if (orderRepository.findByAuctionId(auctionId).isPresent()) {
            throw new IllegalArgumentException("Order already exists for this auction");
        }

        Order order = OrderMapper.toEntity(auction, buyerId, dto);
        order.setStatus(OrderStatus.PENDING_SHIPMENT);
        orderRepository.save(order);

        return ApiResponse.success(OrderMapper.toResponseDTO(order), "Order created successfully");
    }

    @Transactional
    public ApiResponse<OrderResponseDTO> ship(UUID orderId, ShipOrderDTO dto, String sellerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getSellerId().equals(sellerId)) {
            throw new AccessDeniedException("Only the seller can mark the order as shipped");
        }

        if (order.getStatus() != OrderStatus.PENDING_SHIPMENT) {
            throw new IllegalArgumentException("Only PENDING_SHIPMENT orders can be shipped");
        }

        order.setCarrier(dto.getCarrier());
        order.setTrackingNumber(dto.getTrackingNumber());
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        return ApiResponse.success(OrderMapper.toResponseDTO(order), "Order shipped successfully");
    }

    @Transactional
    public ApiResponse<OrderResponseDTO> confirmDelivery(UUID orderId, String buyerId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getBuyerId().equals(buyerId)) {
            throw new AccessDeniedException("Only the buyer can confirm delivery");
        }

        if (order.getStatus() != OrderStatus.SHIPPED) {
            throw new IllegalArgumentException("Only SHIPPED orders can be marked as DELIVERED");
        }

        order.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(order);

        return ApiResponse.success(OrderMapper.toResponseDTO(order), "Order delivered successfully");
    }

    public ApiResponse<List<OrderPublicResponseDTO>> myPurchases(String buyerId) {
        List<Order> orders = orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);

        List<OrderPublicResponseDTO> dtos = orders.stream().map(o -> {
            UserPublicDTO seller = resolveUser(o.getSellerId());
            UserPublicDTO buyer = resolveUser(o.getBuyerId());
            ProductPublicDTO product = resolveProduct(o.getProductId());
            return OrderMapper.toResponseDTOPublic(o, seller, buyer, product);
        }).toList();

        return ApiResponse.success(dtos, "Purchases retrieved successfully");
    }

    public ApiResponse<List<OrderPublicResponseDTO>> mySales(String sellerId) {
        List<Order> orders = orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);

        List<OrderPublicResponseDTO> dtos = orders.stream().map(o -> {
            UserPublicDTO seller = resolveUser(o.getSellerId());
            UserPublicDTO buyer = resolveUser(o.getBuyerId());
            ProductPublicDTO product = resolveProduct(o.getProductId());
            return OrderMapper.toResponseDTOPublic(o, seller, buyer, product);
        }).toList();

        return ApiResponse.success(dtos, "Sales retrieved successfully");
    }

    public ApiResponse<OrderPublicResponseDTO> get(UUID orderId, String requesterId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        boolean allowed = order.getBuyerId().equals(requesterId) || order.getSellerId().equals(requesterId);
        if (!allowed) {
            throw new AccessDeniedException("You are not allowed to view this order");
        }

        UserPublicDTO seller = resolveUser(order.getSellerId());
        UserPublicDTO buyer = resolveUser(order.getBuyerId());
        ProductPublicDTO product = resolveProduct(order.getProductId());

        return ApiResponse.success(
                OrderMapper.toResponseDTOPublic(order, seller, buyer, product),
                "Order retrieved successfully"
        );
    }

    private UserPublicDTO resolveUser(String userId) {
        ApiResponse<UserPublicDTO> res = userClient.findOne(userId);
        if (res == null || !res.isSuccess() || res.getData() == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return res.getData();
    }

    private ProductPublicDTO resolveProduct(UUID productId) {
        ApiResponse<ProductPublicDTO> res = productClient.findProduct(productId);
        if (res == null || !res.isSuccess() || res.getData() == null) {
            throw new ResourceNotFoundException("Product not found");
        }
        return res.getData();
    }
}