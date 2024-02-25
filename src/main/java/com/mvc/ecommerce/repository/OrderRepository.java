package com.mvc.ecommerce.repository;

import com.mvc.ecommerce.entity.Account;
import com.mvc.ecommerce.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    //   Order findByUsername(String username);
    Order findByAccount(Account account);

    Optional<Order> findTopByAccountAndStatusOrderByCreateDateDesc(Account account, String status);

    List<Order> findAll();

    // order has status not equal to "pending" or "confirmed"
    List<Order> findAllByStatusNot(String status);

    List<Order> findAllByStatus(String status);
}