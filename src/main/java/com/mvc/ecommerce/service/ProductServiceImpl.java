package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Category;
import com.mvc.ecommerce.entity.Product;
import com.mvc.ecommerce.repository.CategoryRepository;
import com.mvc.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    private String uploadPath = System.getProperty("user.dir") + "/src/main/resources/static/" + "images";


    @Override
    public Page<Product> getAllProductsByPageAndAvailable(Pageable pageRequest) {
        return productRepository.findByAvailableTrue(pageRequest);
    }

    @Override
    public Page<Product> getAllProductsByPage(Pageable pageRequest) {
        return productRepository.findAll(pageRequest);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long productId) {
        return productRepository.findProductById(productId);
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Long productId) {
        //find the product by id
        Product product = productRepository.findProductById(productId);
        // set the product to unavailable
        product.setAvailable(false);
        // save the product
        productRepository.save(product);
    }

    @Override
    public Page<Product> getProductsByCategoryId(Long categoryId, Pageable pageable) {
        return productRepository.findAllByCategoryIdAndAvailableIsTrue(categoryId, pageable);
    }

    @Override
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchProducts(keyword, pageable);
    }

    @Override
    public Page<Product> getAllProductsSortedByPriceAsc(Pageable pageable) {
        return productRepository.findAllAvailableOrderByPriceAsc(pageable);
    }

    @Override
    public Page<Product> getAllProductsSortedByPriceDesc(Pageable pageable) {
        return productRepository.findAllAvailableOrderByPriceDesc(pageable);
    }

    @Override
    public String saveProductImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        // add try catch block to handle the exception
        try {
            //Generate unique file name
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Create the directory if it doesn't exist
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the upload directory with its original filename
            Path filePath = Paths.get(uploadPath + File.separator + file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Construct and return the URL of the saved image
            return "/images/" + file.getOriginalFilename();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not save the file: " + file.getOriginalFilename());
        }

    }

    @Override
    public void addProduct(String name, String imageUrl, double price, boolean available, Long categoryId) {
        // Create a new Product object
        Product product = new Product();
        product.setName(name);
        product.setImage(imageUrl);
        product.setPrice(price);
        product.setAvailable(available);

        // Assuming you have a method to fetch Category by ID
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID"));
        product.setCategory(category);

        // Save the product to the database
        productRepository.save(product);
    }

    @Override
    public Product updateProductById(Long id, Product product) {
        Product existingProduct = productRepository.findProductById(id);
        if (existingProduct == null) {
            throw new IllegalArgumentException("Product not found");
        }
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setAvailable(product.isAvailable());
        existingProduct.setImage(product.getImage());
        existingProduct.setCategory(product.getCategory());
        return productRepository.save(existingProduct);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }
}
