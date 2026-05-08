package com.example.features.payment.entity;

import com.example.features.payment.enums.PaymentStatus;
import com.example.features.payment.enums.PaymentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entité représentant une transaction Campay en cours ou finalisée.
 * Permet le suivi des dépôts, retraits et paiements de loyer via Orange Money.
 */
@Entity
@Table(name = "campay_transaction")
@Getter
@Setter
public class CampayTransaction {

    /** Référence unique fournie par Campay */
    @Id
    @Column(name = "campay_reference", nullable = false)
    private String campayReference;

    /** Référence externe que nous générons (ex: UUID) */
    @Column(name = "external_reference")
    private String externalReference;

    @Column(name = "amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Numéro Orange Money du client */
    @Column(name = "phone_number")
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PaymentType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    /** Référence du client qui initie la transaction */
    @Column(name = "client_reference")
    private String clientReference;

    /** ID du bail (uniquement pour PAIEMENT_LOYER) */
    @Column(name = "bail_id")
    private Long bailId;

    /** Référence du bailleur (uniquement pour PAIEMENT_LOYER) */
    @Column(name = "bailleur_reference")
    private String bailleurReference;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    private void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

