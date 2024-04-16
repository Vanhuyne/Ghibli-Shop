package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.*;
import com.mvc.ecommerce.service.AccountService;
import com.mvc.ecommerce.service.CartService;
import com.mvc.ecommerce.service.CategoryService;
import com.mvc.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;
    private final AccountService accountService;
    private final HttpSession httpSession;
    private final CategoryService categoryService;
    private final CartService cartService;

    @GetMapping()
    public String getAllProducts(Model model, @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size) {
        Account loggedInUser = getLoggedInUser();
        addCartSizeToModel(model, loggedInUser);

        Page<Product> products = productService.getAllProductsByPageAndAvailable(PageRequest.of(page, size));
        List<Category> categories = categoryService.getAllCategories();

        addAttributesToModel(model, loggedInUser, products, categories);

        return "user/products";
    }

    private Account getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            Account loggedInUser = accountService.findByUsername(authentication.getName());
            httpSession.setAttribute("loggedInUser", loggedInUser);
            httpSession.setMaxInactiveInterval(36000);
            return loggedInUser;
        }
        return null;
    }

    private void addAttributesToModel(Model model, Account loggedInUser, Page<Product> products, List<Category> categories) {
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("products", products.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("currentPage", products.getNumber());
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalItems", products.getTotalElements());
    }

    @GetMapping("/filter")
    public String getProductsByCategoryId(@RequestParam(name = "categoryId") Long categoryId, Model model, @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "8") int size) {
        Account loggedInUser = getLoggedInUser();
        Page<Product> products = productService.getProductsByCategoryId(categoryId, PageRequest.of(page, size));
        List<Category> categories = categoryService.getAllCategories();

        model.addAttribute("sizeCart", loggedInUser != null ? cartService.getSizeCart(loggedInUser.getUsername()) : null);
        addAttributesToModel(model, loggedInUser, products, categories);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("filter", true);

        return "user/products";
    }

    @GetMapping("/search")
    public String searchProducts(@RequestParam("keyword") String keyword,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "8") int size,
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

    @GetMapping("/sortByPrice")
    public String getProductsSortedByPrice(
            Model model, @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size, @RequestParam(defaultValue = "asc") String sort) {
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
