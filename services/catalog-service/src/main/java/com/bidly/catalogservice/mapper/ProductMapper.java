package com.bidly.catalogservice.mapper;

import com.bidly.catalogservice.dto.product.ProductDTO;
import com.bidly.catalogservice.dto.product.ProductResponseDTO;
import com.bidly.catalogservice.entity.Product;
import com.bidly.catalogservice.entity.ProductImage;
import com.bidly.common.dto.UserPublicDTO;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class ProductMapper {

    public static ProductResponseDTO toResponseDto(Product product, UserPublicDTO dtoSeller) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .user(dtoSeller)
                .description(product.getDescription())
                .condition(product.getCondition())
                .status(product.getStatus())
                .categoryName(product.getCategory() != null ? product.getCategory().getName() : null)
                .imageUrls(product.getImages().stream()
                        .map(ProductImageMapper::toDto)
                        .collect(Collectors.toList()))
                .deleted(product.isDeleted())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public static Product toEntity(ProductDTO dto, String userId) {
        return Product.builder()
                .sellerId(userId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .condition(dto.getCondition())
                .build();
    }
}
