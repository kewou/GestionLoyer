package com.example.features.payment;

import com.example.exceptions.BusinessException;
import com.example.features.payment.dto.PaymentRequestDto;
import com.example.features.payment.dto.PaymentResponseDto;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.service.PaymentService;
import com.example.security.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de paiement pour le bailleur.
 * - Retrait du solde vers son Orange Money
 * - Vérification de statut
 * - Historique des transactions Campay
 */
@RestController
@RequestMapping("/bailleur/users/{reference}/payment")
@Slf4j
@Tag(name = "Paiement Bailleur", description = "Retrait du solde vers Orange Money")
public class BailleurPaymentController {

    private final PaymentService paymentService;

    public BailleurPaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/retrait")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Retirer son solde vers Orange Money",
            description = "Transfère le solde disponible du bailleur vers son compte OM")
    public ResponseEntity<PaymentResponseDto> initierRetrait(
            @PathVariable String reference,
            @Valid @RequestBody PaymentRequestDto request) throws BusinessException {
        return ResponseEntity.ok(paymentService.initierRetrait(reference, request));
    }

    @GetMapping("/status/{campayRef}")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Vérifier le statut d'une transaction Campay")
    public ResponseEntity<PaymentResponseDto> verifierStatut(
            @PathVariable String reference,
            @PathVariable String campayRef) throws BusinessException {
        return ResponseEntity.ok(paymentService.verifierStatut(campayRef));
    }

    @GetMapping("/historique")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Historique des transactions Campay du bailleur")
    public ResponseEntity<List<CampayTransaction>> historique(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getHistoriqueTransactions(reference));
    }
}

