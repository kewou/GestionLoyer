package com.example.features.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registre SSE : chaque transaction Campay en attente peut avoir un émetteur SSE
 * enregistré par le frontend. Dès que le webhook confirme le statut final,
 * on pousse l'événement et on libère l'émetteur.
 */
@Service
@Slf4j
public class PaymentNotificationService {

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Enregistre un émetteur SSE pour la transaction donnée.
     * Timeout 3 min — largement supérieur au max polling (60 s).
     */
    public SseEmitter register(String campayRef) {
        SseEmitter emitter = new SseEmitter(180_000L);
        emitters.put(campayRef, emitter);
        emitter.onCompletion(() -> emitters.remove(campayRef));
        emitter.onTimeout(() -> emitters.remove(campayRef));
        emitter.onError(e -> emitters.remove(campayRef));
        log.debug("SSE enregistré pour campayRef={}", campayRef);
        return emitter;
    }

    /**
     * Pousse le statut final au client connecté (appelé depuis traiterWebhook).
     */
    public void notifier(String campayRef, String status, BigDecimal solde) {
        SseEmitter emitter = emitters.remove(campayRef);
        if (emitter == null) {
            log.debug("Aucun émetteur SSE pour campayRef={} (client non connecté ou déjà notifié)", campayRef);
            return;
        }
        try {
            Map<String, Object> data = new java.util.HashMap<>();
            data.put("status", status);
            if (solde != null) {
                data.put("solde", solde);
            }
            emitter.send(SseEmitter.event()
                    .name("payment-status")
                    .data(objectMapper.writeValueAsString(data), MediaType.APPLICATION_JSON));
            emitter.complete();
            log.info("SSE envoyé : campayRef={}, status={}", campayRef, status);
        } catch (IOException e) {
            log.warn("Erreur envoi SSE pour campayRef={}: {}", campayRef, e.getMessage());
        }
    }
}

