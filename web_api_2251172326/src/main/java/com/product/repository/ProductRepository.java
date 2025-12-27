package com.product.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.product.model.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Search by name, description, brand
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> search(@Param("keyword") String keyword, Pageable pageable);

    // Find by Category
    Page<Product> findByCategory(String category, Pageable pageable);

    // Find by Price Range
    Page<Product> findByPriceBetween(Double minPrice, Double maxPrice, Pageable pageable);

    // Find Available Only
    Page<Product> findByIsAvailableTrue(Pageable pageable);

    // Complex Filter (Example: category + price + available) - can be done with
    // Specifications, but strict requirement uses simple list/filter.
    // For "Search Advanced" endpoint, we might need more dynamic queries or
    // Specifications.
    // But detailed spec says: "GET /api/products — list with pagination, search
    // (name/description/brand), filter (category, price range, available_only)."
    // We can use Specifications or just individual methods calling.
    // Let's stick to JpaRepository methods for now.
}
