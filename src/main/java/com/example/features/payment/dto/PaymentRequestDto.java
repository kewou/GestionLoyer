package com.example.features.payment.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @NotNull(message = "Le montant est obligatoire")
    @DecimalMin(value = "10", message = "Le montant minimum est de 10 XAF")
    @DecimalMax(value = "25", message = "Le montant maximum autorisé en environnement de test est de 25 XAF")
    private BigDecimal amount;

    /**
     * Numéro Orange Money (optionnel, utilise celui du profil si absent)
     */
    private String phoneOm;

    /**
     * ID du bail (uniquement pour paiement de loyer)
     */
    private Long bailId;
}
