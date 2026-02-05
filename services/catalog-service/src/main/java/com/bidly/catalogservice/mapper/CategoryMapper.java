package com.bidly.catalogservice.mapper;


import com.bidly.catalogservice.dto.category.CategoryDTO;
import com.bidly.catalogservice.dto.category.CategoryResponseDTO;
import com.bidly.catalogservice.entity.Category;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Component
@Builder
public class CategoryMapper {

    public static CategoryDTO toDto(Category category) {

        return CategoryDTO.builder()
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    public static CategoryResponseDTO toResponseDto(Category category) {

        return CategoryResponseDTO.builder()
                .name(category.getName())
                .description(category.getDescription())
                .deleted(category.isDeleted())
                .build();
    }

    public static Category toEntity(CategoryDTO dto) {

        return Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }
}
