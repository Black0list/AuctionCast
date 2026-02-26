package com.bidly.coreservice.mapper;

import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.coreservice.dto.order.CreateOrderDTO;
import com.bidly.coreservice.dto.order.OrderPublicResponseDTO;
import com.bidly.coreservice.dto.order.OrderResponseDTO;
import com.bidly.coreservice.entity.Auction;
import com.bidly.coreservice.entity.Order;

public class OrderMapper {

    private OrderMapper() {}

    public static Order toEntity(Auction auction, String buyerId, CreateOrderDTO dto) {
        return Order.builder()
                .auctionId(auction.getId())
                .productId(auction.getProductId())
                .sellerId(auction.getSellerId())
                .buyerId(buyerId)
                .amount(auction.getCurrentPrice())
                .shippingFullName(dto.getShippingFullName())
                .shippingPhone(dto.getShippingPhone())
                .shippingAddressLine1(dto.getShippingAddressLine1())
                .shippingAddressLine2(dto.getShippingAddressLine2())
                .shippingCity(dto.getShippingCity())
                .shippingState(dto.getShippingState())
                .shippingPostalCode(dto.getShippingPostalCode())
                .shippingCountry(dto.getShippingCountry())
                .build();
    }

    public static OrderResponseDTO toResponseDTO(Order o) {
        return OrderResponseDTO.builder()
                .id(o.getId())
                .auctionId(o.getAuctionId())
                .productId(o.getProductId())
                .sellerId(o.getSellerId())
                .buyerId(o.getBuyerId())
                .amount(o.getAmount())
                .shippingFullName(o.getShippingFullName())
                .shippingPhone(o.getShippingPhone())
                .shippingAddressLine1(o.getShippingAddressLine1())
                .shippingAddressLine2(o.getShippingAddressLine2())
                .shippingCity(o.getShippingCity())
                .shippingState(o.getShippingState())
                .shippingPostalCode(o.getShippingPostalCode())
                .shippingCountry(o.getShippingCountry())
                .carrier(o.getCarrier())
                .trackingNumber(o.getTrackingNumber())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }

    public static OrderPublicResponseDTO toResponseDTOPublic(
            Order o,
            UserPublicDTO seller,
            UserPublicDTO buyer,
            ProductPublicDTO product
    ) {
        return OrderPublicResponseDTO.builder()
                .id(o.getId())
                .auctionId(o.getAuctionId())
                .productId(o.getProductId())
                .seller(seller)
                .buyer(buyer)
                .product(product)
                .amount(o.getAmount())
                .shippingFullName(o.getShippingFullName())
                .shippingPhone(o.getShippingPhone())
                .shippingAddressLine1(o.getShippingAddressLine1())
                .shippingAddressLine2(o.getShippingAddressLine2())
                .shippingCity(o.getShippingCity())
                .shippingState(o.getShippingState())
                .shippingPostalCode(o.getShippingPostalCode())
                .shippingCountry(o.getShippingCountry())
                .carrier(o.getCarrier())
                .trackingNumber(o.getTrackingNumber())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }
}