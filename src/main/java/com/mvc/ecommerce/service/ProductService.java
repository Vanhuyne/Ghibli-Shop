package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Page<Product> getAllProductsByPage(Pageable pageRequest);

    List<Product> getAllProducts();

    Product getProductById(Long productId);

    void saveProduct(Product product);

    void deleteProduct(Long productId);

    Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> searchProducts(String keyword, Pageable pageable);

    Page<Product> getAllProductsSortedByPriceAsc(Pageable pageable);

    Page<Product> getAllProductsSortedByPriceDesc(Pageable pageable);

    String saveProductImage(MultipartFile file);

    void addProduct(String name, String imageUrl, double price, boolean available, Long categoryId);

    Page<Product> getAllProductsByPageAndAvailable(Pageable pageRequest);


}
