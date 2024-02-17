package com.mvc.ecommerce.controller;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.entity.Order;

import com.mvc.ecommerce.exceptions.NotFoundException;
import com.mvc.ecommerce.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {

    private final HttpSession httpSession;
    private final CartService cartService;

    @GetMapping("/add/{productId}")
    public String addToCart(
            @PathVariable Long productId,
            @RequestParam String username,
            Model model) {

        // Use the 'username' parameter in your logic
        if (username != null) {
            try {
                cartService.addToCart(productId, username, 1);
                model.addAttribute("sizeCart", cartService.getSizeCart(username));
            } catch (NotFoundException e) {
                model.addAttribute("errorMessage", e.getMessage());
                return "redirect:/login";  // assuming you have a user-specific error page
            }
        } else {
            // Redirect to the login page if the username is null
            return "redirect:/login";
        }
        return "redirect:/products";
        //return "redirect:/cart/" + username;
    }

    @GetMapping("/{username}")
    public String viewCart(@PathVariable(required = false) String username, Model model) {
        if (username != null && !username.isEmpty()) {
            Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
            Order order = cartService.viewCart(username);

            if (order != null && !order.getOrderDetails().isEmpty()) {
                // Check if the order status is "Pending"
                if ("Pending".equals(order.getStatus())) {

                    // Calculate total order price only if there are order details
                    double orderTotal = order.getOrderDetails().stream()
                            .mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity())
                            .sum();
                    double orderTotalRound = Math.round(orderTotal * 100.0) / 100.0;

                    model.addAttribute("loggedInUser", loggedInUser);
                    model.addAttribute("order", order);
                    model.addAttribute("orderTotal", orderTotalRound);
                    model.addAttribute("sizeCart", cartService.getSizeCart(username));
                    return "user/cart";
                } else {
                    // Handle the case where the order status is not "Pending"
                    model.addAttribute("loggedInUser", loggedInUser);
                    model.addAttribute("emptyCart", true);
                    model.addAttribute("orderNotPending", true);
                    model.addAttribute("sizeCart", cartService.getSizeCart(username));
                    return "user/cart";
                }
            } else {
                // Handle the case where the shopping cart is empty
                model.addAttribute("loggedInUser", loggedInUser);
                model.addAttribute("emptyCart", true);
                return "user/cart";
            }
        } else {

            return "redirect:/login";
        }
    }

    @GetMapping("/remove/{orderDetailsId}")
    public String removeFromCart(@PathVariable Long orderDetailsId, Model model) {
        // Call the service method to remove the item from the cart
        cartService.removeFromCart(orderDetailsId);
        // Retrieve the username from the session
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        String username = loggedInUser.getUsername();

        // Redirect to the cart view after removing the item
        model.addAttribute("sizeCart", cartService.getSizeCart(username));
        return "redirect:/cart/" + username;
    }

    @PostMapping("/updateQuantity")
    public String updateQuantity(@RequestParam Long orderDetailsId,
                                 @RequestParam int quantityChange, Model model) {
        cartService.updateQuantity(orderDetailsId, quantityChange);

        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        String username = loggedInUser.getUsername();

        model.addAttribute("loggedInUser", loggedInUser);

        return "redirect:/cart/" + username;
    }

    @GetMapping("/confirm-order")
    public String confirmOrder(@RequestParam("orderTotal") Double orderTotal, Model model) {
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        String username = loggedInUser.getUsername();

        Order order = cartService.viewCart(username);

        // Confirm the order during checkout
        //cartService.confirmOrder(order.getId());

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("order", order);
        model.addAttribute("orderTotal", orderTotal);
        model.addAttribute("sizeCart", cartService.getSizeCart(username));
        return "user/confirm_order";
    }

    @GetMapping("/back")
    public String backToCart(Model model) {
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        String username = loggedInUser.getUsername();

        model.addAttribute("loggedInUser", loggedInUser);

        return "redirect:/cart/" + username;
    }

    @GetMapping("/check-out")
    public String checkout(@RequestParam("orderTotal") Double orderTotal, Model model) {
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        String username = loggedInUser.getUsername();

        Order order = cartService.viewCart(username);

        // Confirm the order during checkout
        cartService.confirmOrder(order.getId());

        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("order", order);
        model.addAttribute("orderTotal", orderTotal);
        model.addAttribute("sizeCart", cartService.getSizeCart(username));
        return "user/checkout";
    }
}
