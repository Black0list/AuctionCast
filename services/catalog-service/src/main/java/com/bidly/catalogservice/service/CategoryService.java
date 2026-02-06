package com.bidly.catalogservice.service;


import com.bidly.catalogservice.dto.category.CategoryDTO;
import com.bidly.catalogservice.dto.category.CategoryResponseDTO;
import com.bidly.catalogservice.dto.category.CategoryUpdateDTO;
import com.bidly.catalogservice.entity.Category;
import com.bidly.catalogservice.mapper.CategoryMapper;
import com.bidly.catalogservice.repository.CategoryRepository;
import com.bidly.catalogservice.repository.ProductRepository;
import com.bidly.common.dto.ApiResponse;
import com.bidly.common.exception.ResourceExistsException;
import com.bidly.common.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public ApiResponse<List<CategoryResponseDTO>> list() {
        List<CategoryResponseDTO> categories = categoryRepository.findAll().stream().map(CategoryMapper::toResponseDto).toList();
        if (categories.isEmpty()) {
            throw  new ResourceNotFoundException("No categories found");
        }
        return ApiResponse.success(categories, "Categories retrieved successfully");
    }

    public ApiResponse<List<CategoryDTO>> listActive() {
        List<CategoryDTO> categories = categoryRepository.findAll().stream().filter(c -> !c.isDeleted()).map(CategoryMapper::toDto).toList();
        if (categories.isEmpty()) {
            throw  new ResourceNotFoundException("No categories found");
        }
        return ApiResponse.success(categories, "Categories retrieved successfully");
    }

    public ApiResponse<CategoryResponseDTO> createCategory(CategoryDTO dto) {
        if(categoryRepository.existsByName(dto.getName())) {
            throw new ResourceExistsException("Category with the same name already exists");
        }

        Category category = CategoryMapper.toEntity(dto);
        categoryRepository.save(category);
        return ApiResponse.success(CategoryMapper.toResponseDto(category), "Category created successfully");
    }

    public ApiResponse<CategoryResponseDTO> updateCategory(String name, CategoryUpdateDTO dto) {

        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getName().equals(dto.getName()) && categoryRepository.existsByName(dto.getName())) {
            throw new ResourceExistsException("Category with the same name already exists");
        }

        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setDeleted(dto.isDeleted());
        categoryRepository.save(category);
        return ApiResponse.success(CategoryMapper.toResponseDto(category), "Category updated successfully");
    }

    public ApiResponse<String> deleteCategory(String name, boolean hard) {

        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (hard) {
            if (productRepository.existsByCategory(category)) {
                throw new IllegalStateException("Cannot hard delete category because it has associated products");
            }

            categoryRepository.delete(category);
            return ApiResponse.success("Category hard deleted successfully");
        }

        category.setDeleted(true);
        categoryRepository.save(category);

        return ApiResponse.success("Category soft deleted successfully");
    }

}
