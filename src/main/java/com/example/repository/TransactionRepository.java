package com.example.repository;

import com.example.domain.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sbeezy
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
