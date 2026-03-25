package com.bidly.coreservice.client;

import com.bidly.common.dto.ApiResponse;
import com.bidly.common.dto.ProductPublicDTO;
import com.bidly.security.feign.FeignClientInterceptor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;


@FeignClient(name = "catalog-service", configuration = FeignClientInterceptor.class)
public interface ProductClient {
    @GetMapping("/products/public/{productId}")
    ApiResponse<ProductPublicDTO> findProduct(@PathVariable("productId") UUID productId);

    @GetMapping("/products/{productId}/isProductOwner/{userId}")
    ApiResponse<Boolean> isProductOwner(@PathVariable("productId") UUID productId, @PathVariable("userId") String userId);

    @GetMapping("/products/count/active")
    ApiResponse<Long> countActiveProducts();

    @org.springframework.web.bind.annotation.PutMapping("/products/{productId}/status/{status}")
    ApiResponse<Object> updateStatus(
            @PathVariable("productId") UUID productId,
            @PathVariable("status") com.bidly.common.enums.ProductStatus status
    );
}
