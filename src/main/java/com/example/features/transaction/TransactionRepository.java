package com.example.features.transaction;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.bail.Bail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * @author Sbeezy
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByBail(Bail bail);

    List<Transaction> findByBail_Appart(Appart appart);

    List<Transaction> findByBailAndDateBetween(Bail bail, LocalDate start, LocalDate end);
}
