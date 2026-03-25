package com.bidly.catalogservice.mapper;


import com.bidly.catalogservice.dto.product.ProductImageDTO;
import com.bidly.catalogservice.entity.ProductImage;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
@Builder
public class ProductImageMapper {

    public static ProductImageDTO toDto(ProductImage entity) {
        return ProductImageDTO.builder()
                .imageUrl(entity.getImageUrl())
                .isCover(entity.isCover())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
