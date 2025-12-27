package com.travelapp.repository;

import com.travelapp.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUserId(Long userId);
    List<Borrow> findByBookId(Long bookId);
    List<Borrow> findByStatus(String status);
    List<Borrow> findByUserIdAndStatusIn(Long userId, List<String> statuses);
    boolean existsByUserIdAndBookIdAndReturnDateIsNull(Long userId, Long bookId);
    long countByUserIdAndReturnDateIsNull(Long userId);
    boolean existsByBookIdAndReturnDateIsNull(Long bookId);
    // ...existing code...
}
