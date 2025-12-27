package com.product.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.product.model.Customer;
import com.product.model.Order;
import com.product.model.OrderItem;
import com.product.model.Product;
import com.product.repository.OrderRepository;
import com.product.repository.ProductRepository;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ProductRepository productRepository;

    @Transactional
    public Order createOrder(Customer customer, List<OrderItem> items, String paymentMethod, String address,
            String notes) throws Exception {
        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderNumber(generateOrderNumber());
        order.setOrderDate(new Date());
        order.setStatus("pending");
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("unpaid");
        order.setShippingAddress(address);
        order.setNotes(notes);
        order.setShippingFee(30000.0);

        double subtotal = 0;

        for (OrderItem item : items) {
            Product p = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new Exception("Product not found: " + item.getProduct().getId()));

            if (p.getStock() < item.getQuantity()) {
                throw new Exception("Insufficient stock for product: " + p.getName());
            }

            // Reduce stock
            p.setStock(p.getStock() - item.getQuantity());
            productRepository.save(p);

            item.setPrice(p.getPrice()); // Set price at time of order
            item.setProduct(p); // re-attach attached entity
            order.addOrderItem(item);
            subtotal += item.getPrice() * item.getQuantity();
        }

        order.setSubtotal(subtotal);
        order.setTotal(subtotal + order.getShippingFee());

        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }

    public Page<Order> getOrdersByCustomer(Customer customer, String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByCustomerAndStatus(customer, status, pageable);
        }
        return orderRepository.findByCustomer(customer, pageable);
    }

    public List<Order> getOrdersByStatus(String status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional
    public Order updateOrderStatus(Long id, String newStatus) throws Exception {
        Order order = getOrderById(id);
        if (order == null)
            throw new Exception("Order not found");

        String oldStatus = order.getStatus();

        if ("cancelled".equals(newStatus) && !"cancelled".equals(oldStatus)) {
            // Restore stock
            for (OrderItem item : order.getOrderItems()) {
                Product p = item.getProduct();
                p.setStock(p.getStock() + item.getQuantity());
                productRepository.save(p);
            }
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public Order payOrder(Long id, String paymentMethod) throws Exception {
        Order order = getOrderById(id);
        if (order == null)
            throw new Exception("Order not found");

        if (paymentMethod != null && !paymentMethod.isEmpty()) {
            order.setPaymentMethod(paymentMethod);
        }

        order.setPaymentStatus("paid");
        return orderRepository.save(order);
    }

    private String generateOrderNumber() {
        return "ORD-" + System.currentTimeMillis() + "-" + (int) (Math.random() * 1000);
    }
}
