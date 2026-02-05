package com.bidly.catalogservice.controller;


import com.bidly.catalogservice.dto.category.CategoryDTO;
import com.bidly.catalogservice.dto.category.CategoryResponseDTO;
import com.bidly.catalogservice.dto.category.CategoryUpdateDTO;
import com.bidly.catalogservice.service.CategoryService;
import com.bidly.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    public final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<CategoryResponseDTO>> list() {
        return categoryService.list();
    }

    @GetMapping("/active")
    public ApiResponse<List<CategoryDTO>> listActive() {
        return categoryService.listActive();
    }

    @PostMapping
    public ApiResponse<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryDTO dto) {
        return categoryService.createCategory(dto);
    }

    @PutMapping("/{name}")
    public ApiResponse<CategoryResponseDTO> updateCategory(@PathVariable String name, @Valid @RequestBody CategoryUpdateDTO dto) {
        return categoryService.updateCategory(name, dto);
    }

    @DeleteMapping("/{name}")
    public ApiResponse<String> deleteCategory(@PathVariable String name, @RequestParam boolean hard) {
        return categoryService.deleteCategory(name, hard);
    }
}
