package com.bidly.catalogservice.dto.product;

import com.bidly.common.enums.ProductCondition;
import com.bidly.common.enums.ProductStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDTO {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title can have at most 255 characters")
    private String title;

    @Size(max = 2000, message = "Description can have at most 2000 characters")
    private String description;

    @NotNull(message = "Condition is required")
    private ProductCondition condition;

    @NotNull(message = "Status is required")
    private ProductStatus status;

    @NotNull(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Product status is required")
    private Boolean deleted;

    private List<MultipartFile> images;
}
