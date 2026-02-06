package com.bidly.catalogservice.controller;

import com.bidly.catalogservice.dto.product.ProductDTO;
import com.bidly.catalogservice.dto.product.ProductResponseDTO;
import com.bidly.catalogservice.dto.product.ProductUpdateDTO;
import com.bidly.catalogservice.service.ProductService;
import com.bidly.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductResponseDTO>> list() {
        return productService.list();
    }

    @GetMapping("/active")
    public ApiResponse<List<ProductResponseDTO>> listActive() {
        return productService.listActive();
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDTO> getProduct(@PathVariable UUID id) {
        return productService.getProduct(id);
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<ProductResponseDTO> createProduct(@Valid @ModelAttribute ProductDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return productService.createProduct(dto, jwt.getSubject());
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ApiResponse<ProductResponseDTO> updateProduct(@PathVariable UUID id, @Valid @ModelAttribute ProductUpdateDTO dto) {
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean hard) {
        return productService.deleteProduct(id, hard);
    }
}
