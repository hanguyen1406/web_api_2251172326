package com.product.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.product.model.Product;
import com.product.repository.ProductRepository;

@Service
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.search(keyword, pageable);
    }

    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    public Page<Product> getProductsByPriceRange(Double min, Double max, Pageable pageable) {
        return productRepository.findByPriceBetween(min, max, pageable);
    }

    public Page<Product> getAvailableProducts(Pageable pageable) {
        return productRepository.findByIsAvailableTrue(pageable);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product details) {
        Product product = getProductById(id);
        if (product != null) {
            product.setName(details.getName());
            product.setDescription(details.getDescription());
            product.setPrice(details.getPrice());
            product.setCategory(details.getCategory());
            product.setBrand(details.getBrand());
            product.setStock(details.getStock());
            product.setImageUrl(details.getImageUrl());
            product.setAvailable(details.isAvailable());
            return productRepository.save(product);
        }
        return null;
    }

    @Autowired
    com.product.repository.OrderItemRepository orderItemRepository;

    public boolean deleteProduct(Long id) {
        if (productRepository.existsById(id)) {
            // Check if product is in order items of orders that are NOT "delivered"
            long count = orderItemRepository.countByProductIdAndOrder_StatusNot(id, "delivered");
            if (count > 0) {
                throw new RuntimeException(
                        "Cannot delete product directly. It is part of existing orders that are not delivered yet.");
            }
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
