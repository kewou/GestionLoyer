package com.example.features.payment.service;

import com.example.exceptions.BusinessException;
import com.example.features.bail.Bail;
import com.example.features.bail.BailRepository;
import com.example.features.payment.campay.*;
import com.example.features.payment.dto.PaymentRequestDto;
import com.example.features.payment.dto.PaymentResponseDto;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.enums.PaymentStatus;
import com.example.features.payment.enums.PaymentType;
import com.example.features.payment.repository.CampayTransactionRepository;
import com.example.features.transaction.Transaction;
import com.example.features.transaction.TransactionRepository;
import com.example.features.transaction.TransactionType;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;
import static com.example.exceptions.BusinessException.BusinessErrorType.OTHER;

/**
 * Service de paiement gérant les dépôts, retraits et paiements de loyer via Campay / Orange Money.
 */
@Service
@Slf4j
@Transactional
public class PaymentService {

    private final CampayApiService campayApiService;
    private final CampayTransactionRepository campayTransactionRepository;
    private final ClientRepository clientRepository;
    private final BailRepository bailRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentNotificationService paymentNotificationService;

    @Value("${campay.webhook-url:}")
    private String webhookUrl;

    @Value("${campay.redirect-url:https://beezyweb.net}")
    private String redirectUrl;

    public PaymentService(CampayApiService campayApiService,
                          CampayTransactionRepository campayTransactionRepository,
                          ClientRepository clientRepository,
                          BailRepository bailRepository,
                          TransactionRepository transactionRepository,
                          PaymentNotificationService paymentNotificationService) {
        this.campayApiService = campayApiService;
        this.campayTransactionRepository = campayTransactionRepository;
        this.clientRepository = clientRepository;
        this.bailRepository = bailRepository;
        this.transactionRepository = transactionRepository;
        this.paymentNotificationService = paymentNotificationService;
    }

    // ─────────────────────────────────────────────────────────
    //  DÉPÔT : le client crédite son solde sur l'appli
    // ─────────────────────────────────────────────────────────

    /**
     * Initie un dépôt via Orange Money.
     * Le client reçoit un push USSD sur son téléphone pour confirmer.
     */
    public PaymentResponseDto initierDepot(String clientRef, PaymentRequestDto request) throws BusinessException {
        Client client = getClient(clientRef);
        String phone = resolvePhone(request.getPhoneOm(), client.getPhoneOm(), "dépôt");

        String externalRef = UUID.randomUUID().toString();

        CampayCollectRequest collectRequest = CampayCollectRequest.builder()
                .amount(request.getAmount().toPlainString())
                .currency("XAF")
                .from(phone)
                .description("Dépôt BeezyWeb - " + client.getName() + " " + client.getLastName())
                .externalReference(externalRef)
                .redirectUrl(redirectUrl)
                .webhook(webhookUrl)
                .build();

        CampayCollectResponse collectResponse = campayApiService.collect(collectRequest);

        CampayTransaction campayTx = new CampayTransaction();
        campayTx.setCampayReference(collectResponse.getReference());
        campayTx.setExternalReference(externalRef);
        campayTx.setAmount(request.getAmount());
        campayTx.setPhoneNumber(phone);
        campayTx.setType(PaymentType.DEPOT);
        campayTx.setStatus(PaymentStatus.PENDING);
        campayTx.setClientReference(clientRef);
        campayTransactionRepository.save(campayTx);

        log.info("Dépôt initié pour le client {} : campayRef={}", clientRef, collectResponse.getReference());

        return PaymentResponseDto.builder()
                .campayReference(collectResponse.getReference())
                .status("PENDING")
                .message("Confirmez le paiement de " + request.getAmount() + " XAF sur votre téléphone Orange Money")
                .type("DEPOT")
                .build();
    }

    // ─────────────────────────────────────────────────────────
    //  PAIEMENT DE LOYER : le locataire paie son loyer
    // ─────────────────────────────────────────────────────────

    /**
     * Initie le paiement d'un loyer via Orange Money.
     * L'argent arrive sur le compte OM de l'application,
     * puis le solde du bailleur est crédité.
     */
    public PaymentResponseDto initierPaiementLoyer(String locataireRef, Long bailId,
                                                   PaymentRequestDto request) throws BusinessException {
        Client locataire = getClient(locataireRef);
        String phone = resolvePhone(request.getPhoneOm(), locataire.getPhoneOm(), "paiement de loyer");

        Bail bail = bailRepository.findById(bailId)
                .orElseThrow(() -> new BusinessException("Bail introuvable", NOT_FOUND));

        // Vérifier que ce bail appartient bien à ce locataire
        if (!bail.getLocataire().getReference().equals(locataireRef)) {
            throw new BusinessException("Vous n'êtes pas locataire de ce bail", OTHER);
        }

        Client bailleur = bail.getAppart().getBailleur();
        String externalRef = UUID.randomUUID().toString();

        String description = String.format("Loyer %s - appart %s - %s %s",
                LocalDate.now().getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH),
                bail.getAppart().getNom(),
                locataire.getName(), locataire.getLastName());

        CampayCollectRequest collectRequest = CampayCollectRequest.builder()
                .amount(request.getAmount().toPlainString())
                .currency("XAF")
                .from(phone)
                .description(description)
                .externalReference(externalRef)
                .redirectUrl(redirectUrl)
                .webhook(webhookUrl)
                .build();

        CampayCollectResponse collectResponse = campayApiService.collect(collectRequest);

        CampayTransaction campayTx = new CampayTransaction();
        campayTx.setCampayReference(collectResponse.getReference());
        campayTx.setExternalReference(externalRef);
        campayTx.setAmount(request.getAmount());
        campayTx.setPhoneNumber(phone);
        campayTx.setType(PaymentType.PAIEMENT_LOYER);
        campayTx.setStatus(PaymentStatus.PENDING);
        campayTx.setClientReference(locataireRef);
        campayTx.setBailId(bailId);
        campayTx.setBailleurReference(bailleur.getReference());
        campayTransactionRepository.save(campayTx);

        log.info("Paiement loyer initié : locataire={}, bail={}, campayRef={}",
                locataireRef, bailId, collectResponse.getReference());

        return PaymentResponseDto.builder()
                .campayReference(collectResponse.getReference())
                .status("PENDING")
                .message("Confirmez le paiement de " + request.getAmount() + " XAF sur votre téléphone Orange Money")
                .type("PAIEMENT_LOYER")
                .build();
    }

    // ─────────────────────────────────────────────────────────
    //  RETRAIT : le bailleur retire son solde vers son OM
    // ─────────────────────────────────────────────────────────

    /**
     * Initie un retrait du solde du bailleur vers son compte Orange Money.
     */
    public PaymentResponseDto initierRetrait(String bailleurRef, PaymentRequestDto request) throws BusinessException {
        Client bailleur = getClient(bailleurRef);
        String phone = resolvePhone(request.getPhoneOm(), bailleur.getPhoneOm(), "retrait");

        BigDecimal solde = bailleur.getSolde() != null ? bailleur.getSolde() : BigDecimal.ZERO;
        if (solde.compareTo(request.getAmount()) < 0) {
            throw new BusinessException(
                    "Solde insuffisant. Solde disponible : " + solde + " XAF", OTHER);
        }

        String externalRef = UUID.randomUUID().toString();

        CampayWithdrawRequest withdrawRequest = CampayWithdrawRequest.builder()
                .amount(request.getAmount().toPlainString())
                .currency("XAF")
                .to(phone)
                .description("Retrait BeezyWeb - " + bailleur.getName() + " " + bailleur.getLastName())
                .externalReference(externalRef)
                .build();

        CampayWithdrawResponse withdrawResponse = campayApiService.withdraw(withdrawRequest);

        // Débiter le solde immédiatement (l'API Campay withdraw est généralement synchrone)
        bailleur.setSolde(solde.subtract(request.getAmount()));
        clientRepository.save(bailleur);

        CampayTransaction campayTx = new CampayTransaction();
        campayTx.setCampayReference(withdrawResponse.getReference());
        campayTx.setExternalReference(externalRef);
        campayTx.setAmount(request.getAmount());
        campayTx.setPhoneNumber(phone);
        campayTx.setType(PaymentType.RETRAIT);
        campayTx.setStatus(PaymentStatus.SUCCESSFUL); // retrait synchrone
        campayTx.setClientReference(bailleurRef);
        campayTransactionRepository.save(campayTx);

        log.info("Retrait effectué pour bailleur {} : montant={} XAF, campayRef={}",
                bailleurRef, request.getAmount(), withdrawResponse.getReference());

        return PaymentResponseDto.builder()
                .campayReference(withdrawResponse.getReference())
                .status("SUCCESSFUL")
                .message("Retrait de " + request.getAmount() + " XAF effectué vers votre Orange Money")
                .solde(bailleur.getSolde())
                .type("RETRAIT")
                .build();
    }

    // ─────────────────────────────────────────────────────────
    //  VÉRIFICATION DE STATUT (polling depuis le front)
    // ─────────────────────────────────────────────────────────

    /**
     * Vérifie le statut d'une transaction Campay et met à jour les soldes si nécessaire.
     */
    public PaymentResponseDto verifierStatut(String campayRef) throws BusinessException {
        CampayTransaction campayTx = campayTransactionRepository.findByCampayReference(campayRef)
                .orElseThrow(() -> new BusinessException("Transaction introuvable : " + campayRef, NOT_FOUND));

        // Si déjà traité, on renvoie l'état courant
        if (campayTx.getStatus() == PaymentStatus.SUCCESSFUL
                || campayTx.getStatus() == PaymentStatus.FAILED) {
            return buildStatusResponse(campayTx);
        }

        // Interroger Campay
        CampayStatusResponse campayStatus = campayApiService.checkStatus(campayRef);
        String campayState = campayStatus.getStatus();

        if ("SUCCESSFUL".equals(campayState)) {
            campayTx.setStatus(PaymentStatus.SUCCESSFUL);
            traiterSucces(campayTx);
        } else if ("FAILED".equals(campayState)) {
            campayTx.setStatus(PaymentStatus.FAILED);
            log.warn("Paiement Campay échoué : ref={}", campayRef);
        }

        campayTransactionRepository.save(campayTx);
        return buildStatusResponse(campayTx);
    }

    // ─────────────────────────────────────────────────────────
    //  WEBHOOK Campay (appelé par les serveurs Campay)
    // ─────────────────────────────────────────────────────────

    /**
     * Traite la notification webhook de Campay.
     */
    public void traiterWebhook(CampayStatusResponse webhookPayload) {
        if (webhookPayload == null || webhookPayload.getReference() == null) {
            log.warn("Webhook Campay reçu avec payload invalide");
            return;
        }

        String campayRef = webhookPayload.getReference();
        campayTransactionRepository.findByCampayReference(campayRef).ifPresentOrElse(campayTx -> {
            if (campayTx.getStatus() != PaymentStatus.PENDING) {
                log.debug("Transaction {} déjà traitée, webhook ignoré", campayRef);
                return;
            }

            if ("SUCCESSFUL".equals(webhookPayload.getStatus())) {
                campayTx.setStatus(PaymentStatus.SUCCESSFUL);
                traiterSucces(campayTx);
            } else if ("FAILED".equals(webhookPayload.getStatus())) {
                campayTx.setStatus(PaymentStatus.FAILED);
                log.warn("Paiement Campay échoué via webhook : ref={}", campayRef);
            }

            campayTransactionRepository.save(campayTx);

            // Notifier le frontend via SSE
            BigDecimal soldeClient = clientRepository.findByReference(campayTx.getClientReference())
                    .map(Client::getSolde)
                    .orElse(null);
            paymentNotificationService.notifier(campayRef, campayTx.getStatus().name(), soldeClient);

        }, () -> log.warn("Webhook Campay : aucune transaction trouvée pour ref={}", campayRef));
    }

    // ─────────────────────────────────────────────────────────
    //  MÉTHODES PRIVÉES
    // ─────────────────────────────────────────────────────────

    /**
     * Traite le succès d'une transaction Campay selon son type.
     */
    private void traiterSucces(CampayTransaction campayTx) {
        switch (campayTx.getType()) {
            case DEPOT -> traiterDepot(campayTx);
            case PAIEMENT_LOYER -> traiterPaiementLoyer(campayTx);
            case RETRAIT -> log.debug("Retrait déjà traité : ref={}", campayTx.getCampayReference());
        }
    }

    private void traiterDepot(CampayTransaction campayTx) {
        clientRepository.findByReference(campayTx.getClientReference()).ifPresent(client -> {
            BigDecimal ancienSolde = client.getSolde() != null ? client.getSolde() : BigDecimal.ZERO;
            client.setSolde(ancienSolde.add(campayTx.getAmount()));
            clientRepository.save(client);
            log.info("Dépôt crédité : client={}, montant={}, nouveau solde={}",
                    campayTx.getClientReference(), campayTx.getAmount(), client.getSolde());
        });
    }

    private void traiterPaiementLoyer(CampayTransaction campayTx) {
        // 1. Créditer le solde du bailleur
        if (campayTx.getBailleurReference() != null) {
            clientRepository.findByReference(campayTx.getBailleurReference()).ifPresent(bailleur -> {
                BigDecimal ancienSolde = bailleur.getSolde() != null ? bailleur.getSolde() : BigDecimal.ZERO;
                bailleur.setSolde(ancienSolde.add(campayTx.getAmount()));
                clientRepository.save(bailleur);
                log.info("Loyer crédité au bailleur {} : montant={}, nouveau solde={}",
                        campayTx.getBailleurReference(), campayTx.getAmount(), bailleur.getSolde());
            });
        }

        // 2. Créer une Transaction liée au bail
        if (campayTx.getBailId() != null) {
            bailRepository.findById(campayTx.getBailId()).ifPresent(bail -> {
                Transaction tx = new Transaction();
                tx.setBail(bail);
                tx.setMontant(campayTx.getAmount().intValue());
                tx.setDate(LocalDate.now());
                tx.setType(TransactionType.PAIEMENT_LOYER_OM);
                tx.setCampayReference(campayTx.getCampayReference());
                transactionRepository.save(tx);
                log.info("Transaction loyer créée : bailId={}, montant={}",
                        campayTx.getBailId(), campayTx.getAmount());
            });
        }
    }

    private Client getClient(String reference) throws BusinessException {
        return clientRepository.findByReference(reference)
                .orElseThrow(() -> new BusinessException("Utilisateur introuvable : " + reference, NOT_FOUND));
    }

    private String resolvePhone(String requestPhone, String profilePhone, String operation) throws BusinessException {
        String phone = requestPhone != null && !requestPhone.isBlank() ? requestPhone : profilePhone;
        if (phone == null || phone.isBlank()) {
            throw new BusinessException(
                    "Numéro Orange Money requis pour le " + operation
                            + ". Renseignez votre numéro dans votre profil ou dans la requête.", OTHER);
        }
        return phone;
    }

    private PaymentResponseDto buildStatusResponse(CampayTransaction campayTx) {
        String message = switch (campayTx.getStatus()) {
            case SUCCESSFUL -> "Paiement effectué avec succès";
            case FAILED -> "Paiement échoué ou annulé";
            case PENDING -> "Paiement en attente de confirmation";
        };
        return PaymentResponseDto.builder()
                .campayReference(campayTx.getCampayReference())
                .status(campayTx.getStatus().name())
                .message(message)
                .type(campayTx.getType().name())
                .build();
    }

    /**
     * Retourne l'historique des transactions Campay d'un client.
     */
    public List<CampayTransaction> getHistoriqueTransactions(String clientRef) {
        return campayTransactionRepository.findByClientReferenceOrderByCreatedAtDesc(clientRef);
    }
}

