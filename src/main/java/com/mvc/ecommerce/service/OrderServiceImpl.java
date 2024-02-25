package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Order;
import com.mvc.ecommerce.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Override
    public List<Order> getAllOrders() {
        return orderRepository.findAllByStatusNot("Deleted");
    }

    @Override
    public List<Order> ListOrderConfirmed(String status) {
        return orderRepository.findAllByStatus(status);
    }

    @Override
    public void deleteOrderById(Long id) {
        //set status to deleted
        Order order = orderRepository.findById(id).get();
        order.setStatus("Deleted");
        orderRepository.save(order);
    }

}
