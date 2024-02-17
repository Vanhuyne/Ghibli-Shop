package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Category;

import java.util.List;
import java.util.Optional;


public interface CategoryService {
    List<Category> getAllCategories();

    Category getCategoryById(Long categoryId);

    Category saveCategory(Category category);

    void deleteCategory(Long categoryId);
}
