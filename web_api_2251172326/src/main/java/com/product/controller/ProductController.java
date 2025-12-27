package com.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.product.model.Product;
import com.product.service.ProductService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    ProductService productService;

    @GetMapping
    public Page<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean availableOnly) {

        Pageable pageable = PageRequest.of(page, size);

        if (search != null && !search.isEmpty()) {
            return productService.searchProducts(search, pageable);
        }
        if (category != null && !category.isEmpty()) {
            return productService.getProductsByCategory(category, pageable);
        }
        if (minPrice != null && maxPrice != null) {
            return productService.getProductsByPriceRange(minPrice, maxPrice, pageable);
        }
        if (availableOnly != null && availableOnly) {
            return productService.getAvailableProducts(pageable);
        }

        return productService.getAllProducts(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.status(404)
                    .body(new com.product.model.MessageResponse("Không tìm thấy sản phẩm với ID: " + id));
        }
        return ResponseEntity.ok(product);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        if (updated == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            if (productService.deleteProduct(id)) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest()
                    .body(new com.product.model.MessageResponse("Không thể xóa: Sản phẩm không tồn tại"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new com.product.model.MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/search")
    public Page<Product> searchAdvanced(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(keyword, PageRequest.of(page, size));
    }
}
