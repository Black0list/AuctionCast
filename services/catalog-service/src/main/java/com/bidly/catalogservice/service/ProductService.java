package com.bidly.catalogservice.service;

import com.bidly.catalogservice.client.UserClient;
import com.bidly.catalogservice.dto.product.ProductDTO;
import com.bidly.catalogservice.dto.product.ProductResponseDTO;
import com.bidly.catalogservice.dto.product.ProductUpdateDTO;
import com.bidly.catalogservice.entity.Category;
import com.bidly.catalogservice.entity.Product;
import com.bidly.catalogservice.entity.ProductImage;
import com.bidly.catalogservice.mapper.ProductMapper;
import com.bidly.catalogservice.repository.CategoryRepository;
import com.bidly.catalogservice.repository.ProductRepository;
import com.bidly.catalogservice.resolver.UserPublicProfileResolver;
import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.UserPublicDTO;
import com.bidly.common.enums.ProductStatus;
import com.bidly.common.exception.ResourceNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final UserClient userClient;
    private final UserPublicProfileResolver userPublicProfileResolver;

    public ApiResponse<List<ProductResponseDTO>> list() {
        List<String> sellersIds = productRepository.findAll().stream()
                .map(Product::getSellerId)
                .toList();

        Map<String, UserPublicDTO> sellersDTOs = resolver(sellersIds);

        List<Product> products = productRepository.findAll().stream().toList();
        

        List<ProductResponseDTO> productDTOs = products.stream()
                .map(p -> {
                    UserPublicDTO sellerDto = sellersDTOs.get(p.getSellerId());
                    return ProductMapper.toResponseDto(p, sellerDto);
                })
                .toList();

        return ApiResponse.success(productDTOs, "Products retrieved successfully");
    }

    public ApiResponse<List<ProductResponseDTO>> listActive() {
        List<String> sellersIds = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE && !p.isDeleted())
                .map(Product::getSellerId)
                .toList();

        Map<String, UserPublicDTO> sellersDTOs = resolver(sellersIds);

        List<Product> products = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE && !p.isDeleted())
                .toList();


        List<ProductResponseDTO> productDTOs = products.stream()
                .map(p -> {
                    UserPublicDTO sellerDto = sellersDTOs.get(p.getSellerId());
                    return ProductMapper.toResponseDto(p, sellerDto);
                })
                .toList();

        return ApiResponse.success(productDTOs, "Active products retrieved successfully");
    }

    public Map<String, UserPublicDTO> resolver(List<String> ids){
        return userPublicProfileResolver.resolve(ids);
    }

    public ApiResponse<ProductResponseDTO> getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Map<String, UserPublicDTO> sellerDTOMap = resolver(List.of(product.getSellerId()));

        return ApiResponse.success(ProductMapper.toResponseDto(product, sellerDTOMap.get(product.getSellerId())), "Product retrieved successfully");
    }

    @Transactional
    public ApiResponse<ProductResponseDTO> createProduct(ProductDTO dto, String userId) {
        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        boolean isVerifiedSeller = userClient.isSeller(userId).getData();

        if (!isVerifiedSeller) {
            throw new AccessDeniedException("User is not a verified seller. Please apply to become a seller first.");
        }

        Product product = ProductMapper.toEntity(dto, userId);
        product.setStatus(ProductStatus.DRAFT);
        product.setCategory(category);
        product.setImages(new ArrayList<>());

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (MultipartFile imageFile : dto.getImages()) {
                String imageUrl = fileStorageService.saveFile(imageFile, "products-images");
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageUrl)
                        .product(product)
                        .isCover(product.getImages().isEmpty())
                        .build();
                product.getImages().add(productImage);
            }
        }

        Map<String, UserPublicDTO> sellerDTOMap = resolver(List.of(product.getSellerId()));

        productRepository.save(product);
        return ApiResponse.success(ProductMapper.toResponseDto(product, sellerDTOMap.get(userId)), "Product created successfully");
    }

    @Transactional
    public ApiResponse<ProductResponseDTO> updateProduct(UUID id, ProductUpdateDTO dto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalArgumentException("Product can only be updated if it is in DRAFT status");
        }

        Category category = categoryRepository.findByName(dto.getCategoryName())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setCondition(dto.getCondition());
        product.setStatus(dto.getStatus());
        product.setCategory(category);
        product.setDeleted(dto.getDeleted());

        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            for (ProductImage oldImage : product.getImages()) {
                fileStorageService.deleteFile(oldImage.getImageUrl());
            }
            product.getImages().clear();

            for (MultipartFile imageFile : dto.getImages()) {
                String imageUrl = fileStorageService.saveFile(imageFile, "products-images");
                ProductImage productImage = ProductImage.builder()
                        .imageUrl(imageUrl)
                        .product(product)
                        .isCover(product.getImages().isEmpty())
                        .build();
                product.getImages().add(productImage);
            }
        }

        Map<String, UserPublicDTO> sellerDTOMap = resolver(List.of(product.getSellerId()));

        productRepository.save(product);
        return ApiResponse.success(ProductMapper.toResponseDto(product, sellerDTOMap.get(product.getSellerId())), "Product updated successfully");
    }

    @Transactional
    public ApiResponse<String> deleteProduct(UUID id, boolean hard) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStatus() != ProductStatus.DRAFT) {
            throw new IllegalArgumentException("Product can only be deleted if it is in DRAFT status");
        }

        if (hard) {
            for (ProductImage image : product.getImages()) {
                fileStorageService.deleteFile(image.getImageUrl());
            }

            productRepository.delete(product);
            return ApiResponse.success("Product hard deleted successfully");
        }

        product.setDeleted(true);
        product.setStatus(ProductStatus.ARCHIVED);
        productRepository.save(product);
        return ApiResponse.success("Product soft deleted successfully");
    }

    public ApiResponse<Boolean> isProductOwner(UUID id, String userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        return ApiResponse.success(product.getSellerId().equals(userId), "Ownership check completed");
    }

    public ApiResponse<List<ProductResponseDTO>> myProducts(String userId) {
        List<Product> products = productRepository.findBySellerId(userId);

        Map<String, UserPublicDTO> sellerDTOMap = resolver(List.of(userId));

        List<ProductResponseDTO> productDTOs = products.stream()
                .map(p -> ProductMapper.toResponseDto(p, sellerDTOMap.get(userId)))
                .toList();

        return ApiResponse.success(productDTOs, "User's products retrieved successfully");
    }

    public ApiResponse<Long> countActiveProducts() {
        long count = productRepository.findAll().stream()
                .filter(p -> p.getStatus() == ProductStatus.ACTIVE && !p.isDeleted())
                .count();
        return ApiResponse.success(count, "Active products counted");
    }

    @Transactional
    public void deleteUserProducts(String userId) {
        List<Product> products = productRepository.findBySellerId(userId);
        for (Product product : products) {
            for (ProductImage image : product.getImages()) {
                fileStorageService.deleteFile(image.getImageUrl());
            }
        }
        productRepository.deleteAll(products);
    }

    @Transactional
    public ApiResponse<ProductResponseDTO> updateStatus(UUID id, ProductStatus status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setStatus(status);
        productRepository.save(product);

        Map<String, UserPublicDTO> sellerDTOMap = resolver(List.of(product.getSellerId()));
        return ApiResponse.success(ProductMapper.toResponseDto(product, sellerDTOMap.get(product.getSellerId())), "Product status updated successfully");
    }
}
