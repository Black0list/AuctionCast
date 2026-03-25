package com.bidly.catalogservice.dto.product;

import com.bidly.catalogservice.entity.ProductImage;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.ProductCondition;
import com.bidly.common.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductImageDTO {
    private String imageUrl;
    boolean isCover;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
