package com.travelapp.controller;

import com.travelapp.model.Borrow;
import com.travelapp.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {
    @Autowired
    private BorrowService borrowService;

    @GetMapping
    public Page<Borrow> getAllBorrows(Pageable pageable) {
        return borrowService.getAllBorrows(pageable);
    }

    @GetMapping("/user/{userId}")
    public List<Borrow> getBorrowsByUser(@PathVariable Long userId) {
        return borrowService.getBorrowsByUserId(userId);
    }

    @GetMapping("/book/{bookId}")
    public List<Borrow> getBorrowsByBook(@PathVariable Long bookId) {
        return borrowService.getBorrowsByBookId(bookId);
    }

    @GetMapping("/status/{status}")
    public List<Borrow> getBorrowsByStatus(@PathVariable String status) {
        return borrowService.getBorrowsByStatus(status);
    }

    @PostMapping
    public ResponseEntity<?> borrowBook(@RequestParam Long userId, @RequestParam Long bookId, @RequestParam int daysToBorrow, @RequestParam(required = false) String notes) {
        try {
            Borrow borrow = borrowService.borrowBook(userId, bookId, daysToBorrow, notes);
            return ResponseEntity.status(HttpStatus.CREATED).body(borrow);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id) {
        try {
            Borrow borrow = borrowService.returnBook(id);
            return ResponseEntity.ok(borrow);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBorrow(@PathVariable Long id) {
        borrowService.deleteBorrow(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/active")
    public ResponseEntity<List<Borrow>> getActiveBorrows(@RequestParam(required = false) String status) {
        List<Borrow> borrows;
        if (status != null) {
            borrows = borrowService.getBorrowsByStatus(status);
        } else {
            borrows = borrowService.getBorrowsByStatus("borrowed");
        }
        return ResponseEntity.ok(borrows);
    }

    @GetMapping("/user/{userId}/borrows")
    public ResponseEntity<List<Borrow>> getUserBorrows(@PathVariable Long userId) {
        List<Borrow> borrows = borrowService.getBorrowsByUserId(userId);
        return ResponseEntity.ok(borrows);
    }

    @GetMapping("/book/{bookId}/borrows")
    public ResponseEntity<List<Borrow>> getBookBorrows(@PathVariable Long bookId) {
        List<Borrow> borrows = borrowService.getBorrowsByBookId(bookId);
        return ResponseEntity.ok(borrows);
    }
}
