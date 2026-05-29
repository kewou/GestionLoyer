package com.example.features.payment;

import com.example.exceptions.BusinessException;
import com.example.features.payment.dto.PaymentRequestDto;
import com.example.features.payment.dto.PaymentResponseDto;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.service.PaymentNotificationService;
import com.example.features.payment.service.PaymentService;
import com.example.security.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * Endpoints de paiement pour le locataire.
 * - Dépôt (crédite son propre solde)
 * - Paiement de loyer (crédite le bailleur + crée une Transaction)
 * - Vérification de statut
 */
@RestController
@RequestMapping("/locataire/users/{reference}/payment")
@Slf4j
@Tag(name = "Paiement Locataire", description = "Dépôt et paiement de loyer via Orange Money")
public class LocatairePaymentController {

    private final PaymentService paymentService;
    private final PaymentNotificationService paymentNotificationService;

    public LocatairePaymentController(PaymentService paymentService, PaymentNotificationService paymentNotificationService) {
        this.paymentService = paymentService;
        this.paymentNotificationService = paymentNotificationService;
    }

    @PostMapping("/depot")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Initier un dépôt Orange Money", description = "Crédite le solde du locataire après confirmation OM")
    public ResponseEntity<PaymentResponseDto> initierDepot(
            @PathVariable String reference,
            @Valid @RequestBody PaymentRequestDto request) throws BusinessException {
        return ResponseEntity.ok(paymentService.initierDepot(reference, request));
    }

    @PostMapping("/payer-loyer/{bailId}")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Payer son loyer via Orange Money",
            description = "L'argent arrive sur le compte OM de l'app et crédite le solde du bailleur")
    public ResponseEntity<PaymentResponseDto> payerLoyer(
            @PathVariable String reference,
            @PathVariable Long bailId,
            @Valid @RequestBody PaymentRequestDto request) throws BusinessException {
        request.setBailId(bailId);
        return ResponseEntity.ok(paymentService.initierPaiementLoyer(reference, bailId, request));
    }

    @GetMapping("/status/{campayRef}")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Vérifier le statut d'un paiement",
            description = "Interroge Campay et met à jour le solde si le paiement est confirmé")
    public ResponseEntity<PaymentResponseDto> verifierStatut(
            @PathVariable String reference,
            @PathVariable String campayRef) throws BusinessException {
        return ResponseEntity.ok(paymentService.verifierStatut(campayRef));
    }

    @GetMapping("/historique")
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "Historique des transactions Campay du locataire")
    public ResponseEntity<List<CampayTransaction>> historique(@PathVariable String reference) {
        return ResponseEntity.ok(paymentService.getHistoriqueTransactions(reference));
    }

    @GetMapping(value = "/notifications/{campayRef}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize(SecurityRule.CONNECTED_OR_ADMIN)
    @Operation(summary = "SSE — notifier en temps réel du statut final d'un paiement",
            description = "S'abonne aux événements webhook Campay pour une transaction donnée. " +
                    "Événement 'payment-status' émis une seule fois avec {status, solde}.")
    public SseEmitter subscribePaymentStatus(
            @PathVariable String reference,
            @PathVariable String campayRef) {
        return paymentNotificationService.register(campayRef);
    }
}

