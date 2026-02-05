package com.bidly.catalogservice.controller;


import com.bidly.catalogservice.entity.Product;
import com.bidly.catalogservice.service.ProductService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@Builder
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
}
