package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Category;
import com.mvc.ecommerce.repository.CategoryRepository;
import com.mvc.ecommerce.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            return categoryOptional.get();
        } else {
            throw new RuntimeException("Category not found for id :: " + categoryId);
        }
    }

    @Override
    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            if (productRepository.countByCategory(category) > 0) {
                throw new RuntimeException("Cannot delete category. There are products associated with this category.");
            }
            categoryRepository.delete(category);
        } else {
            throw new RuntimeException("Category not found for id :: " + categoryId);
        }
    }
}
