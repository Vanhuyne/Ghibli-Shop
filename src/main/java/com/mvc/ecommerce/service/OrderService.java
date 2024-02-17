package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();

    List<Order> ListOrderConfirmed(String status);
}
