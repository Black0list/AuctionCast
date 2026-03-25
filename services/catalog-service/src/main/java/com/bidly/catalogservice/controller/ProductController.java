package com.bidly.catalogservice.controller;

import com.bidly.catalogservice.dto.product.ProductDTO;
import com.bidly.catalogservice.dto.product.ProductResponseDTO;
import com.bidly.catalogservice.dto.product.ProductUpdateDTO;
import com.bidly.catalogservice.service.ProductService;
import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.ProductImagePublicDTO;
import com.bidly.common.dto.ProductPublicDTO;
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

    @GetMapping("/my-products")
    public ApiResponse<List<ProductResponseDTO>> myProducts(@AuthenticationPrincipal Jwt jwt) {
        return productService.myProducts(jwt.getSubject());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponseDTO> getProduct(@PathVariable("id") UUID id) {
        return productService.getProduct(id);
    }

    @GetMapping("/{id}/isProductOwner/{userId}")
    public ApiResponse<Boolean> isProductOwner(@PathVariable("id") UUID id, @PathVariable("userId") String userId) {
        return productService.isProductOwner(id, userId);
    }

    @GetMapping("/public/{id}")
    public ApiResponse<ProductPublicDTO> getPublicProduct(@PathVariable("id") UUID id) {
        ApiResponse<ProductResponseDTO> productDTO = productService.getProduct(id);
        
        List<ProductImagePublicDTO> imageUrls = null;
        if (productDTO.getData().getImageUrls() != null) {
            imageUrls = productDTO.getData().getImageUrls().stream()
                    .map(img -> ProductImagePublicDTO.builder()
                            .imageUrl(img.getImageUrl())
                            .build())
                    .toList();
        }

        ProductPublicDTO publicProductDTO = ProductPublicDTO.builder()
                .id(productDTO.getData().getId())
                .title(productDTO.getData().getTitle())
                .description(productDTO.getData().getDescription())
                .coverImage(imageUrls != null && !imageUrls.isEmpty() ? imageUrls.get(0).getImageUrl() : null)
                .imageUrls(imageUrls)
                .status(productDTO.getData().getStatus())
                .build();

        return ApiResponse.success(publicProductDTO, "Public product retrieved successfully");
    }



    @PostMapping(consumes = {"multipart/form-data"})
    public ApiResponse<ProductResponseDTO> createProduct(@Valid @ModelAttribute ProductDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return productService.createProduct(dto, jwt.getSubject());
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ApiResponse<ProductResponseDTO> updateProduct(@PathVariable("id") UUID id, @Valid @ModelAttribute ProductUpdateDTO dto) {
        return productService.updateProduct(id, dto);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable("id") UUID id, @RequestParam(value = "hard", defaultValue = "false") boolean hard) {
        return productService.deleteProduct(id, hard);
    }

    @GetMapping("/count/active")
    public ApiResponse<Long> countActiveProducts() {
        return productService.countActiveProducts();
    }

    @PutMapping("/{id}/status/{status}")
    public ApiResponse<ProductResponseDTO> updateStatus(@PathVariable("id") UUID id, @PathVariable("status") com.bidly.common.enums.ProductStatus status) {
        return productService.updateStatus(id, status);
    }
}
