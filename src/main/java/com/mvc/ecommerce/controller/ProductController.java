package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.*;
import com.mvc.ecommerce.service.CartService;
import com.mvc.ecommerce.service.CategoryService;
import com.mvc.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class ProductController {
    private final ProductService productService;

    private final HttpSession httpSession;

    private final CategoryService categoryService;
    private final CartService cartService;

    @GetMapping("/products")
    public String getAllProducts(Model model, @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "4") int size) {
        // Retrieve a list of products from the ProductService
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        addCartSizeToModel(model, loggedInUser);

        Page<Product> products = productService.getAllProductsByPageAndAvailable(PageRequest.of(page, size));
        List<Category> categories = categoryService.getAllCategories();

        // Add the logged-in user, page of products, and categories to the Thymeleaf model
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("products", products.getContent());  // Extract the content from the Page
        model.addAttribute("categories", categories);

        // Add pagination-related attributes to the model

        model.addAttribute("currentPage", products.getNumber());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());


        return "user/products";
    }

    @GetMapping("/products/filter")
    public String getProductsByCategoryId(@RequestParam(name = "categoryId") Long categoryId, Model model) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        List<Category> categories = categoryService.getAllCategories();

        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        return "user/products"; // Assuming "products" is the Thymeleaf template for displaying products
    }

    @GetMapping("/products/search")
    public String searchProducts(@RequestParam("keyword") String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "4") int size,
                                 Model model) {
        // Retrieve a list of products from the ProductService
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        addCartSizeToModel(model, loggedInUser);

        Page<Product> searchResults = productService.searchProducts(keyword, PageRequest.of(page, size));
        List<Product> products = searchResults.getContent();
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("products", products);

        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", searchResults.getTotalPages());

        return "user/products";
    }

    @GetMapping("/products/sortByPrice")
    public String getProductsSortedByPrice(
            Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "4") int size, @RequestParam(defaultValue = "asc") String sort) {
        // Retrieve a list of products from the ProductService
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        addCartSizeToModel(model, loggedInUser);

        Page<Product> products = getSortedProducts(page, size, sort);
        List<Category> categories = categoryService.getAllCategories();


        // Add the logged-in user, page of products, and categories to the Thymeleaf model
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("categories", categories);
        model.addAttribute("products", products);
        model.addAttribute("currentPage", products.getNumber());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
        model.addAttribute("sort", sort);
        return "user/products";
    }

    private Page<Product> getSortedProducts(int page, int size, String sort) {
        if ("desc".equalsIgnoreCase(sort)) {
            return productService.getAllProductsSortedByPriceDesc(PageRequest.of(page, size));
        } else if ("asc".equalsIgnoreCase(sort)) {
            return productService.getAllProductsSortedByPriceAsc(PageRequest.of(page, size));
        } else {
            return productService.getAllProductsByPage(PageRequest.of(page, size));
        }
    }

    private void addCartSizeToModel(Model model, Account loggedInUser) {
        if (loggedInUser != null) {
            String username = loggedInUser.getUsername();
            int sizeCart = cartService.getSizeCart(username);
            model.addAttribute("sizeCart", sizeCart);
        }
    }


}
