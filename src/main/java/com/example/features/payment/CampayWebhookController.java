package com.example.features.payment;

import com.example.features.payment.campay.CampayStatusResponse;
import com.example.features.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Endpoint public recevant les notifications webhook de Campay.
 * Valide la clé webhook configurée (campay.webhook-key) avant de traiter.
 */
@RestController
@RequestMapping("/payment/webhook")
@Slf4j
@Tag(name = "Webhook Campay", description = "Endpoint public pour les notifications Campay")
public class CampayWebhookController {

    private final PaymentService paymentService;

    @Value("${campay.webhook-key:}")
    private String webhookKey;

    public CampayWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/campay")
    @Operation(summary = "Webhook Campay (public)",
            description = "Reçoit les notifications de paiement de Campay, valide la clé, et met à jour les soldes")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody CampayStatusResponse payload) {

        // Validation de la clé webhook si configurée
        if (webhookKey != null && !webhookKey.isBlank()) {
            String receivedKey = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                receivedKey = authHeader.substring(7);
            }
            // Fallback : vérifier dans le payload (certaines implémentations Campay)
            if (receivedKey == null && payload.getVerifyToken() != null) {
                receivedKey = payload.getVerifyToken();
            }
            if (!webhookKey.equals(receivedKey)) {
                log.warn("Webhook Campay rejeté : clé invalide (reçu={})", receivedKey != null ? "***" : "null");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }

        log.info("Webhook Campay reçu : ref={}, status={}", payload.getReference(), payload.getStatus());
        paymentService.traiterWebhook(payload);
        return ResponseEntity.ok().build();
    }
}


