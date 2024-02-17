package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.entity.Product;
import com.mvc.ecommerce.exceptions.NotFoundException;
import com.mvc.ecommerce.service.CartService;
import com.mvc.ecommerce.service.ProductService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products-details")
public class ProductDetailsController {

    private final ProductService productService;
    private final HttpSession httpSession;
    private final CartService cartService;

    @GetMapping("/{productId}")
    public String getPageDetails(@PathVariable Long productId, Model model) {
        Product product = productService.getProductById(productId);

        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            model.addAttribute("sizeCart", cartService.getSizeCart(loggedInUser.getUsername()));
        }
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("product", product);


        return "user/product_details";
    }

    @GetMapping("/addFromProductDetails/{productId}")
    public String addToCart(
            @PathVariable Long productId,
            @RequestParam String username,
            @RequestParam("quantity") Integer quantity,
            Model model) {
        if (username != null) {
            try {
                cartService.addToCart(productId, username, quantity);
            } catch (NotFoundException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "redirect:/login";  // assuming you have a user-specific error page
            }
        } else {
            // Redirect to the login page if the username is null
            return "redirect:/login";
        }
        //return "redirect:/products";
        return "redirect:/cart/" + username;

    }


}
