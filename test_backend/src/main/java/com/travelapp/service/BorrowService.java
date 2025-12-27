package com.travelapp.service;

import com.travelapp.model.Borrow;
import com.travelapp.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {
    @Autowired
    private BorrowRepository borrowRepository;

    public Page<Borrow> getAllBorrows(Pageable pageable) {
        return borrowRepository.findAll(pageable);
    }

    public List<Borrow> getBorrowsByUserId(Long userId) {
        return borrowRepository.findByUserId(userId);
    }

    public List<Borrow> getBorrowsByBookId(Long bookId) {
        return borrowRepository.findByBookId(bookId);
    }

    public List<Borrow> getBorrowsByStatus(String status) {
        return borrowRepository.findByStatus(status);
    }

    public Optional<Borrow> getBorrowById(Long id) {
        return borrowRepository.findById(id);
    }

    @Transactional
    public Borrow saveBorrow(Borrow borrow) {
        return borrowRepository.save(borrow);
    }

    @Transactional
    public void deleteBorrow(Long id) {
        borrowRepository.deleteById(id);
    }

    @Transactional
    public Borrow borrowBook(Long userId, Long bookId, int daysToBorrow, String notes) {
        // Validate daysToBorrow
        if (daysToBorrow < 1 || daysToBorrow > 30) {
            throw new IllegalArgumentException("days_to_borrow must be between 1 and 30");
        }
        // Check user borrow limit
        long activeBorrows = borrowRepository.countByUserIdAndReturnDateIsNull(userId);
        if (activeBorrows >= 5) {
            throw new IllegalStateException("User has reached max active borrows");
        }
        // Check duplicate borrow
        if (borrowRepository.existsByUserIdAndBookIdAndReturnDateIsNull(userId, bookId)) {
            throw new IllegalStateException("User already borrowed this book and hasn't returned it");
        }
        // Check available copies
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new IllegalArgumentException("Book not found"));
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies");
        }
        // Create borrow record
        Borrow borrow = new Borrow();
        borrow.setUser(userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")));
        borrow.setBook(book);
        borrow.setBorrowDate(LocalDateTime.now());
        borrow.setDueDate(LocalDateTime.now().plusDays(daysToBorrow));
        borrow.setStatus("borrowed");
        borrow.setNotes(notes);
        borrowRepository.save(borrow);
        // Update book available copies
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);
        return borrow;
    }

    @Transactional
    public Borrow returnBook(Long borrowId) {
        Borrow borrow = borrowRepository.findById(borrowId).orElseThrow(() -> new IllegalArgumentException("Borrow not found"));
        if (borrow.getReturnDate() != null) {
            throw new IllegalStateException("Book already returned");
        }
        borrow.setReturnDate(LocalDateTime.now());
        borrow.setStatus("returned");
        borrowRepository.save(borrow);
        // Update book available copies
        Book book = borrow.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);
        return borrow;
    }
}
