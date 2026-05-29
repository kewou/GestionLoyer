package com.example.features.payment.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentResponseDto {

    private String campayReference;
    private String status;     // PENDING, SUCCESSFUL, FAILED
    private String message;
    private BigDecimal solde;  // solde mis à jour (si applicable)
    private String type;       // DEPOT, RETRAIT, PAIEMENT_LOYER
}

