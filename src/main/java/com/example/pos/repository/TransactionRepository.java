package com.example.pos.repository;

import com.example.pos.entity.Transaction; // Ensure this import matches the `Transaction` package
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
