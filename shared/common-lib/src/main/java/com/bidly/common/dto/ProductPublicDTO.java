package com.bidly.common.dto;

import com.bidly.common.enums.ProductStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductPublicDTO implements Serializable {
    private UUID id;
    private String title;
    private String description;
    private String coverImage;
    private List<ProductImagePublicDTO> imageUrls;
    private ProductStatus status;
}
