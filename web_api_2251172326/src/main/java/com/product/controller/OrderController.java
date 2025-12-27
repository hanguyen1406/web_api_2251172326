package com.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.product.model.Customer;
import com.product.model.Order;
import com.product.model.OrderItem;
import com.product.model.UserDetailsImpl;
import com.product.service.CustomerService;
import com.product.service.OrderService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    OrderService orderService;

    @Autowired
    CustomerService customerService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createOrder(@RequestBody CreateOrderRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Customer customer = customerService.getCustomerById(userDetails.getId());

            Order order = orderService.createOrder(customer, request.getItems(), request.getPaymentMethod(),
                    request.getShippingAddress(), request.getNotes());
            return ResponseEntity.status(201).body(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        if (order == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMINISTRATOR') or hasRole('USER')")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateOrderStatusRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            boolean isAdmin = userDetails.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR"));

            Order order = orderService.getOrderById(id);
            if (order == null)
                return ResponseEntity.notFound().build();

            // ACL Check
            if (!isAdmin) {
                // User must be owner
                if (!order.getCustomer().getId().equals(userDetails.getId())) {
                    return ResponseEntity.status(403).body("Access Denied: You do not own this order");
                }
                // User can ONLY set status to "cancelled"
                if (!"cancelled".equalsIgnoreCase(request.getStatus())) {
                    return ResponseEntity.status(403).body("Access Denied: Users can only cancel orders");
                }
            }

            Order updated = orderService.updateOrderStatus(id, request.getStatus());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(@PathVariable Long id, @RequestBody PaymentRequest request) {
        try {
            // Validate validation (mock)
            if ("card".equalsIgnoreCase(request.getPaymentMethod())) {
                // Mock validation
                // If we want to simulate failure:
                // throw new Exception("Card validation failed");

                // For now success
            }

            Order updated = orderService.payOrder(id, request.getPaymentMethod());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> getOrders(@RequestParam(required = false) String status) {
        if (status != null) {
            return ResponseEntity.ok(orderService.getOrdersByStatus(status));
        }
        // Return all or paged - simplified for now
        return ResponseEntity.ok().build();
    }
}

class CreateOrderRequest {
    private List<OrderItem> items;
    private String paymentMethod;
    private String shippingAddress;
    private String notes;

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

class UpdateOrderStatusRequest {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

class PaymentRequest {
    @com.fasterxml.jackson.annotation.JsonProperty("payment_method")
    private String paymentMethod;

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
