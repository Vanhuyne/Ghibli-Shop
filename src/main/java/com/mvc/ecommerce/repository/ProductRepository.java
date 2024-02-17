package com.mvc.ecommerce.repository;

import com.mvc.ecommerce.entity.Category;
import com.mvc.ecommerce.entity.Product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllBy(Pageable pageable);

    Page<Product> findByAvailableTrue(Pageable pageable);

    List<Product> findAllByCategoryIdAndAvailableIsTrue(Long categoryId);

    Product findProductById(Long productId);

    @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND p.available = true")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.available = true ORDER BY p.price ASC")
    Page<Product> findAllAvailableOrderByPriceAsc(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.available = true ORDER BY p.price DESC")
    Page<Product> findAllAvailableOrderByPriceDesc(Pageable pageable);

    long countByCategory(Category category);

}
