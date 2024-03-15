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
    public String addToCart(@PathVariable Long productId, @RequestParam String username, Model model) {
        try {
            cartService.addToCart(productId, username, 1);
            model.addAttribute("sizeCart", cartService.getSizeCart(username));
        } catch (NotFoundException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "redirect:/login";
        }
        return "redirect:/products";
    }

    @GetMapping("/{username}")
    public String viewCart(@PathVariable(required = false) String username, Model model) {
        Account loggedInUser = (Account) httpSession.getAttribute("loggedInUser");
        Order order = cartService.viewCart(username);
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("sizeCart", cartService.getSizeCart(username));

        if (order == null || order.getOrderDetails().isEmpty() || !"Pending".equals(order.getStatus())) {
            model.addAttribute("emptyCart", order == null || order.getOrderDetails().isEmpty());
            model.addAttribute("orderNotPending", !"Pending".equals(order.getStatus()));
            return "user/cart";
        }

        double orderTotal = order.getOrderDetails().stream()
                .mapToDouble(orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity())
                .sum();
        model.addAttribute("order", order);
        model.addAttribute("orderTotal", Math.round(orderTotal * 100.0) / 100.0);

        return "user/cart";
    }

    @GetMapping("/remove/{orderDetailsId}")
    public String removeFromCart(@PathVariable Long orderDetailsId, Model model) {

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
