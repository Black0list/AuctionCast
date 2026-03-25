package com.bidly.catalogservice.repository;

import com.bidly.catalogservice.entity.Category;
import com.bidly.catalogservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    List<Product> findBySellerId(String sellerId);
    List<Product> findByCategoryId(Long categoryId);
    boolean existsByCategory(Category category);
    void deleteBySellerId(String sellerId);
}
