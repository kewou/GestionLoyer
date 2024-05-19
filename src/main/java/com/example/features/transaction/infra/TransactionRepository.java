package com.example.features.transaction.infra;

import com.example.features.transaction.domain.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Sbeezy
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
