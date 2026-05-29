package com.example.features.payment.service;

import com.example.exceptions.BusinessException;
import com.example.features.bail.Bail;
import com.example.features.bail.BailRepository;
import com.example.features.payment.campay.*;
import com.example.features.payment.dto.PaymentRequestDto;
import com.example.features.payment.dto.PaymentResponseDto;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.enums.ModeEncaissement;
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

        // Montant du loyer + 1% frais de gestion
        BigDecimal montantLoyer = BigDecimal.valueOf(bail.getAppart().getPrixLoyer());
        BigDecimal fraisGestion = montantLoyer.multiply(BigDecimal.valueOf(0.01)).setScale(0, java.math.RoundingMode.CEILING);
        BigDecimal montantTotal = montantLoyer.add(fraisGestion);

        log.info("Paiement loyer - Calcul : loyer={} XAF, frais gestion (1%)={} XAF, total={} XAF",
                montantLoyer, fraisGestion, montantTotal);

        String description = String.format("Loyer %s - appart %s - %s %s",
                LocalDate.now().getMonth().getDisplayName(java.time.format.TextStyle.FULL, java.util.Locale.FRENCH),
                bail.getAppart().getNom(),
                locataire.getName(), locataire.getLastName());

        CampayCollectRequest collectRequest = CampayCollectRequest.builder()
                .amount(montantTotal.toPlainString())
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
        campayTx.setAmount(montantTotal);
        campayTx.setPhoneNumber(phone);
        campayTx.setType(PaymentType.PAIEMENT_LOYER);
        campayTx.setStatus(PaymentStatus.PENDING);
        campayTx.setClientReference(locataireRef);
        campayTx.setBailId(bailId);
        campayTx.setBailleurReference(bailleur.getReference());
        campayTransactionRepository.save(campayTx);

        log.info("Paiement loyer initié : locataire={}, bail={}, bailleur={}, montantTotal={} XAF, campayRef={}",
                locataireRef, bailId, bailleur.getReference(), montantTotal, collectResponse.getReference());

        return PaymentResponseDto.builder()
                .campayReference(collectResponse.getReference())
                .status("PENDING")
                .message("Confirmez le paiement de " + montantTotal + " XAF sur votre téléphone Orange Money (loyer "
                        + montantLoyer + " XAF + frais de gestion " + fraisGestion + " XAF)")
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

        log.info("Vérification statut transaction : campayRef={}, type={}, statut actuel={}",
                campayRef, campayTx.getType(), campayTx.getStatus());

        // Si déjà traité, on renvoie l'état courant
        if (campayTx.getStatus() == PaymentStatus.SUCCESSFUL
                || campayTx.getStatus() == PaymentStatus.FAILED) {
            log.info("Transaction déjà finalisée : campayRef={}, statut={}", campayRef, campayTx.getStatus());
            return buildStatusResponse(campayTx);
        }

        // Interroger Campay
        CampayStatusResponse campayStatus = campayApiService.checkStatus(campayRef);
        String campayState = campayStatus.getStatus();
        log.info("Réponse Campay pour ref={} : statut={}", campayRef, campayState);

        if ("SUCCESSFUL".equals(campayState)) {
            campayTx.setStatus(PaymentStatus.SUCCESSFUL);
            traiterSucces(campayTx);
            log.info("Transaction confirmée SUCCESSFUL : campayRef={}, type={}", campayRef, campayTx.getType());
        } else if ("FAILED".equals(campayState)) {
            campayTx.setStatus(PaymentStatus.FAILED);
            log.warn("Transaction FAILED : campayRef={}, type={}", campayRef, campayTx.getType());
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
            // Pour PAIEMENT_LOYER : c'est le solde BeezyWeb du bailleur qui est pertinent
            // (le locataire n'a pas de solde sur l'application)
            String refPourSolde = (campayTx.getType() == PaymentType.PAIEMENT_LOYER
                    && campayTx.getBailleurReference() != null)
                    ? campayTx.getBailleurReference()
                    : campayTx.getClientReference();
            BigDecimal solde = clientRepository.findByReference(refPourSolde)
                    .map(Client::getSolde)
                    .orElse(null);
            log.info("SSE notification : campayRef={}, statut={}, solde bailleur={}", campayRef, campayTx.getStatus(), solde);
            paymentNotificationService.notifier(campayRef, campayTx.getStatus().name(), solde);

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

    public void traiterPaiementLoyer(CampayTransaction campayTx) {
        log.info("▶ traiterPaiementLoyer - campayRef={}, locataire={}, bailleurRef={}, montantTotal={} XAF",
                campayTx.getCampayReference(), campayTx.getClientReference(),
                campayTx.getBailleurReference(), campayTx.getAmount());

        if (campayTx.getBailId() == null) {
            log.error("✗ bailId est null pour campayRef={} — impossible de créditer le bailleur",
                    campayTx.getCampayReference());
            return;
        }

        Bail bail = bailRepository.findById(campayTx.getBailId()).orElse(null);
        if (bail == null) {
            log.error("✗ Bail introuvable (id={}) pour campayRef={}", campayTx.getBailId(),
                    campayTx.getCampayReference());
            return;
        }

        // Récupérer le bailleur directement depuis le bail (source de vérité)
        // plutôt que de dépendre de campayTx.bailleurReference qui peut être null
        Client bailleur = bail.getAppart().getBailleur();
        if (bailleur == null) {
            log.error("✗ Bailleur introuvable via bail.appart (bailId={}) pour campayRef={}",
                    campayTx.getBailId(), campayTx.getCampayReference());
            return;
        }

        // Recharger le bailleur depuis le repository pour avoir l'entité gérée par JPA
        Client bailleurManaged = clientRepository.findByReference(bailleur.getReference()).orElse(null);
        if (bailleurManaged == null) {
            log.error("✗ Bailleur non trouvé en base (ref={}) pour campayRef={}",
                    bailleur.getReference(), campayTx.getCampayReference());
            return;
        }

        // Montant du loyer à créditer (sans les 1% de frais de gestion conservés par la plateforme)
        BigDecimal montantLoyer = BigDecimal.valueOf(bail.getAppart().getPrixLoyer());
        log.info("  Bail id={} | appart={} | prixLoyer={} XAF | bailleur={}",
                bail.getId(), bail.getAppart().getNom(), montantLoyer, bailleurManaged.getReference());

        // 1. Créditer le solde BeezyWeb du bailleur
        BigDecimal ancienSolde = bailleurManaged.getSolde() != null ? bailleurManaged.getSolde() : BigDecimal.ZERO;
        BigDecimal nouveauSolde = ancienSolde.add(montantLoyer);
        bailleurManaged.setSolde(nouveauSolde);
        clientRepository.saveAndFlush(bailleurManaged);
        log.info("✔ Solde BeezyWeb bailleur {} mis à jour : {} → {} XAF (+{} XAF)",
                bailleurManaged.getReference(), ancienSolde, nouveauSolde, montantLoyer);

        // Mettre à jour bailleurReference dans la transaction Campay si manquant
        if (campayTx.getBailleurReference() == null) {
            campayTx.setBailleurReference(bailleurManaged.getReference());
            log.warn("  bailleurReference était null sur campayTx, corrigé : {}", bailleurManaged.getReference());
        }

        // 2. Créer une Transaction loyer liée au bail (= 1 mois payé, comme un versement manuel)
        Transaction tx = new Transaction();
        tx.setBail(bail);
        tx.setMontant(montantLoyer.intValue());
        tx.setDate(LocalDate.now());
        tx.setType(TransactionType.PAIEMENT_LOYER_OM);
        tx.setCampayReference(campayTx.getCampayReference());
        transactionRepository.save(tx);
        log.info("✔ Transaction loyer créée : bailId={}, montant={} XAF, date={}, campayRef={}",
                campayTx.getBailId(), montantLoyer, LocalDate.now(), campayTx.getCampayReference());

        // 3. Retrait automatique si le bailleur a activé le mode DIRECT (paiement en une étape)
        if (ModeEncaissement.DIRECT.equals(bailleurManaged.getModeEncaissement())) {
            try {
                String phoneOm = bailleurManaged.getPhoneOm();
                if (phoneOm == null || phoneOm.isBlank()) {
                    log.warn("⚠ Mode DIRECT activé pour bailleur {} mais aucun numéro OM renseigné — retrait automatique ignoré",
                            bailleurManaged.getReference());
                } else {
                    String phoneOmNormalise = normaliserNumero(phoneOm);
                    String externalRefRetrait = UUID.randomUUID().toString();

                    CampayWithdrawRequest withdrawRequest = CampayWithdrawRequest.builder()
                            .amount(montantLoyer.toPlainString())
                            .currency("XAF")
                            .to(phoneOmNormalise)
                            .description("Loyer " + bail.getAppart().getNom() + " - retrait automatique BeezyWeb")
                            .externalReference(externalRefRetrait)
                            .build();

                    CampayWithdrawResponse withdrawResponse = campayApiService.withdraw(withdrawRequest);

                    // Débiter le solde du bailleur (il vient juste d'être crédité du montantLoyer)
                    BigDecimal soldeApresRetrait = nouveauSolde.subtract(montantLoyer);
                    bailleurManaged.setSolde(soldeApresRetrait.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : soldeApresRetrait);
                    clientRepository.saveAndFlush(bailleurManaged);

                    // Enregistrer la transaction Campay de retrait automatique
                    CampayTransaction retraitTx = new CampayTransaction();
                    retraitTx.setCampayReference(withdrawResponse.getReference());
                    retraitTx.setExternalReference(externalRefRetrait);
                    retraitTx.setAmount(montantLoyer);
                    retraitTx.setPhoneNumber(phoneOmNormalise);
                    retraitTx.setType(PaymentType.RETRAIT);
                    retraitTx.setStatus(PaymentStatus.SUCCESSFUL);
                    retraitTx.setClientReference(bailleurManaged.getReference());
                    campayTransactionRepository.save(retraitTx);

                    log.info("✔ Retrait automatique (mode DIRECT) effectué pour bailleur {} : {} XAF → {}",
                            bailleurManaged.getReference(), montantLoyer, phoneOmNormalise);
                }
            } catch (Exception e) {
                // Ne pas faire échouer tout le paiement si le retrait automatique échoue
                log.error("✗ Erreur lors du retrait automatique (mode DIRECT) pour bailleur {} : {}",
                        bailleurManaged.getReference(), e.getMessage(), e);
            }
        } else {
            log.debug("Mode DEUX_ETAPES : bailleur {} conserve son solde BeezyWeb (pas de retrait automatique)",
                    bailleurManaged.getReference());
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
        return normaliserNumero(phone);
    }

    /**
     * Normalise un numéro camerounais vers le format international Campay (237XXXXXXXXX).
     * Accepte : "6XXXXXXXX", "06XXXXXXXX", "+2376XXXXXXXX", "2376XXXXXXXX".
     */
    private String normaliserNumero(String phone) {
        String cleaned = phone.replaceAll("[\\s\\-\\.\\(\\)]", ""); // supprimer espaces/tirets
        if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1); // retirer le +
        }
        if (cleaned.startsWith("237")) {
            return cleaned; // déjà au bon format
        }
        if (cleaned.startsWith("0")) {
            cleaned = cleaned.substring(1); // retirer le 0 initial
        }
        return "237" + cleaned;
    }

    private PaymentResponseDto buildStatusResponse(CampayTransaction campayTx) {
        String message = switch (campayTx.getStatus()) {
            case SUCCESSFUL -> "Paiement effectué avec succès";
            case FAILED -> "Paiement échoué ou annulé";
            case PENDING -> "Paiement en attente de confirmation";
        };

        // Récupérer le solde pertinent selon le type
        BigDecimal solde = null;
        if (campayTx.getStatus() == PaymentStatus.SUCCESSFUL) {
            if (campayTx.getType() == PaymentType.PAIEMENT_LOYER && campayTx.getBailleurReference() != null) {
                solde = clientRepository.findByReference(campayTx.getBailleurReference())
                        .map(Client::getSolde).orElse(null);
            } else {
                solde = clientRepository.findByReference(campayTx.getClientReference())
                        .map(Client::getSolde).orElse(null);
            }
        }

        return PaymentResponseDto.builder()
                .campayReference(campayTx.getCampayReference())
                .status(campayTx.getStatus().name())
                .message(message)
                .type(campayTx.getType().name())
                .solde(solde)
                .build();
    }

    /**
     * Retourne l'historique des transactions Campay d'un client.
     */
    public List<CampayTransaction> getHistoriqueTransactions(String clientRef) {
        return campayTransactionRepository.findByClientReferenceOrderByCreatedAtDesc(clientRef);
    }
}

