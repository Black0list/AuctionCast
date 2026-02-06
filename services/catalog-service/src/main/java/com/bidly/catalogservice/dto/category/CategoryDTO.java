package com.bidly.catalogservice.dto.category;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryDTO {
    @Size(max = 30, message = "Name can have at most 30 characters")
    private String name;

    @Size(max = 500, message = "Description can have at most 500 characters")
    private String description;
}
