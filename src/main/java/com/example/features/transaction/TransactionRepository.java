package com.example.features.transaction;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.bail.Bail;
import com.example.features.logement.Logement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.bail.appart.logement = :logement")
    Long sumMontantByLogement(@Param("logement") Logement logement);

    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.bail = :bail")
    Long sumMontantByBail(@Param("bail") Bail bail);
}


