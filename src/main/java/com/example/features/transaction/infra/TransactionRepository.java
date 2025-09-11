package com.example.features.transaction.infra;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.transaction.domain.entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Sbeezy
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByAppart(Appart appart);
}
