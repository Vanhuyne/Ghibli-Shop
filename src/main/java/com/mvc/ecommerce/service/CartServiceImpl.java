package com.mvc.ecommerce.service;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.entity.Order;
import com.mvc.ecommerce.entity.OrderDetails;
import com.mvc.ecommerce.entity.Product;
import com.mvc.ecommerce.exceptions.NotFoundException;
import com.mvc.ecommerce.repository.AccountRepository;
import com.mvc.ecommerce.repository.OrderDetailsRepository;
import com.mvc.ecommerce.repository.OrderRepository;
import com.mvc.ecommerce.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    @Override
    public void addToCart(Long productId, String username, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow(() ->
                new NotFoundException("Product not found with id: " + productId));

        Order order = getOrCreateOrder(username);

        // Check if the product is already in the order details
        Optional<OrderDetails> existingOrderDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getProduct().equals(product))
                .findFirst();
        if (existingOrderDetail.isPresent()) {
            // If the product is already in the order details, update the quantity
            OrderDetails orderDetail = existingOrderDetail.get();
            orderDetail.setQuantity(orderDetail.getQuantity() + quantity);

        } else {
            OrderDetails orderDetail = new OrderDetails();
            orderDetail.setOrder(order);
            orderDetail.setProduct(product);
            orderDetail.setPrice(product.getPrice());
            orderDetail.setQuantity(quantity);

            orderDetailsRepository.save(orderDetail);
        }
        orderRepository.save(order);
    }

    @Override
    public Order viewCart(String username) {
        return getOrCreateOrder(username);
    }


    private Order getOrCreateOrder(String username) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);

        if (optionalAccount.isPresent()) {
            // Retrieve the latest order with "Pending" status
            Optional<Order> optionalOrder = orderRepository.findTopByAccountAndStatusOrderByCreateDateDesc(
                    optionalAccount.get(), "Pending");

            if (optionalOrder.isPresent()) {
                // If an order with "Pending" status is found, return it
                return optionalOrder.get();
            } else {
                // If no order with "Pending" status is found, create a new order
                Order newOrder = new Order();
                newOrder.setAccount(optionalAccount.get());
                newOrder.setCreateDate(LocalDateTime.now());
                newOrder.setAddress(optionalAccount.get().getAddress());
                newOrder.setOrderDetails(new ArrayList<>());
                // Set an initial status for new orders
                newOrder.setStatus("Pending");

                orderRepository.save(newOrder);

                return newOrder;
            }
        } else {
            // Handle the case where the account is not found
            throw new NotFoundException("Account not found for username: " + username);
        }
    }


    @Override
    public void removeFromCart(Long orderDetailsId) {
        // Find the OrderDetails by ID
        Optional<OrderDetails> optionalOrderDetails = orderDetailsRepository.findById(orderDetailsId);

        if (optionalOrderDetails.isPresent()) {
            OrderDetails orderDetail = optionalOrderDetails.get();
            Order order = orderDetail.getOrder();

            // Remove the OrderDetails from the Order's list
            order.getOrderDetails().remove(orderDetail);

            // Delete the OrderDetails from the repository
            orderDetailsRepository.delete(orderDetail);

            // Update the Order in the repository
            orderRepository.save(order);

        } else {
            // Handle the case where the OrderDetails is not found
            throw new NotFoundException("OrderDetails not found with id: " + orderDetailsId);
        }
    }

    @Override
    @Transactional
    public void updateQuantity(Long orderDetailsId, int quantityChange) {
        Optional<OrderDetails> optionalOrderDetails = orderDetailsRepository.findById(orderDetailsId);

        if (optionalOrderDetails.isPresent()) {
            OrderDetails orderDetail = optionalOrderDetails.get();
            int newQuantity = orderDetail.getQuantity() + quantityChange;

            if (newQuantity > 0) {
                orderDetail.setQuantity(newQuantity);
                orderDetailsRepository.save(orderDetail);
            }
        }
    }

    @Override
    public void confirmOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        // Update the order status to "Confirmed"
        order.setStatus("Confirmed");
        orderRepository.save(order);
    }

    @Override
    public List<OrderDetails> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailsRepository.findByOrder_Id(orderId);
    }

    @Override
    public int getSizeCart(String username) {
        Order order = viewCart(username);
        List<OrderDetails> orderDetailsList = getOrderDetailsByOrderId(order.getId());
        return orderDetailsList.size();
    }
}
