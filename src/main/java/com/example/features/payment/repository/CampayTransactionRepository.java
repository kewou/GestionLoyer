package com.example.features.payment.repository;

import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampayTransactionRepository extends JpaRepository<CampayTransaction, String> {

    Optional<CampayTransaction> findByCampayReference(String campayReference);

    List<CampayTransaction> findByClientReferenceOrderByCreatedAtDesc(String clientReference);

    List<CampayTransaction> findByClientReferenceAndStatus(String clientReference, PaymentStatus status);
}

