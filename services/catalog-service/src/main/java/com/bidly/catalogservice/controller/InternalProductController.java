package com.bidly.catalogservice.controller;

import com.bidly.catalogservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService productService;

    @DeleteMapping("/{userId}")
    public void deleteUserProducts(@PathVariable("userId") String userId) {
        productService.deleteUserProducts(userId);
    }
}
