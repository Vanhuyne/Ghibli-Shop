package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.entity.Order;
import com.mvc.ecommerce.entity.OrderDetails;

import java.util.List;

public interface CartService {
    void addToCart(Long productId, String username, int quantity);

    Order viewCart(String username);

    void removeFromCart(Long orderDetailsId);

    void updateQuantity(Long orderDetailsId, int quantityChange);

    void confirmOrder(Long orderId);

    List<OrderDetails> getOrderDetailsByOrderId(Long orderId);

    int getSizeCart(String username);
}
