package com.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.product.model.Customer;
import com.product.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer(Customer customer, Pageable pageable);

    Page<Order> findByCustomerAndStatus(Customer customer, String status, Pageable pageable);

    List<Order> findByStatus(String status);

    Boolean existsByOrderNumber(String orderNumber);
}
