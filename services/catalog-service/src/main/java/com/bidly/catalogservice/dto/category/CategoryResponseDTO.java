package com.bidly.catalogservice.dto.category;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDTO {
    private String name;
    private String description;
    private boolean deleted;
}
