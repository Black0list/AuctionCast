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
                .shippingAddress(o.getShippingAddressLine1())
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
        OrderPublicResponseDTO.OrderPublicResponseDTOBuilder builder = OrderPublicResponseDTO.builder()
                .id(o.getId())
                .auctionId(o.getAuctionId())
                .productId(o.getProductId())
                .seller(seller)
                .buyer(buyer)
                .product(product)
                .amount(o.getAmount());

        boolean updated = false;
        if (buyer != null && !"Deleted".equals(buyer.getFirstName())) {
            builder.shippingFullName(buyer.getFirstName() + " " + buyer.getLastName())
                   .shippingPhone(buyer.getPhone() != null ? buyer.getPhone() : o.getShippingPhone())
                   .shippingAddress(buyer.getAddressLine1() != null ? buyer.getAddressLine1() : o.getShippingAddressLine1())
                   .shippingAddressLine2(buyer.getAddressLine2() != null ? buyer.getAddressLine2() : o.getShippingAddressLine2())
                   .shippingCity(buyer.getCity() != null ? buyer.getCity() : o.getShippingCity())
                   .shippingState(buyer.getState() != null ? buyer.getState() : o.getShippingState())
                   .shippingPostalCode(buyer.getPostalCode() != null ? buyer.getPostalCode() : o.getShippingPostalCode())
                   .shippingCountry(buyer.getCountry() != null ? buyer.getCountry() : o.getShippingCountry());
            updated = true;
        }

        if (!updated) {
            builder.shippingFullName(o.getShippingFullName())
                   .shippingPhone(o.getShippingPhone())
                   .shippingAddress(o.getShippingAddressLine1())
                   .shippingAddressLine2(o.getShippingAddressLine2())
                   .shippingCity(o.getShippingCity())
                   .shippingState(o.getShippingState())
                   .shippingPostalCode(o.getShippingPostalCode())
                   .shippingCountry(o.getShippingCountry());
        }

        return builder.carrier(o.getCarrier())
                .trackingNumber(o.getTrackingNumber())
                .status(o.getStatus())
                .createdAt(o.getCreatedAt())
                .build();
    }
}