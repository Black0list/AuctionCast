package com.bidly.catalogservice.dto.product;


import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.ProductCondition;
import com.bidly.common.enums.ProductStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ProductResponseDTO {
    private UUID id;
    private UserPublicDTO user;
    private String title;
    private String description;
    private ProductCondition condition;
    private ProductStatus status;
    private String categoryName;
    private List<String> imageUrls;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
