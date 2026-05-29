package com.example.features.payment.service;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.bail.Bail;
import com.example.features.bail.BailRepository;
import com.example.features.payment.campay.CampayApiService;
import com.example.features.payment.campay.CampayStatusResponse;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.enums.PaymentStatus;
import com.example.features.payment.enums.PaymentType;
import com.example.features.payment.repository.CampayTransactionRepository;
import com.example.features.transaction.Transaction;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService — paiement loyer OM")
class PaymentServiceTest {

    @Mock private CampayApiService campayApiService;
    @Mock private CampayTransactionRepository campayTransactionRepository;
    @Mock private ClientRepository clientRepository;
    @Mock private BailRepository bailRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private PaymentNotificationService paymentNotificationService;

    private PaymentService paymentService;

    /** Constantes de test */
    private static final String CAMPAY_REF     = "campay-ref-test-001";
    private static final String BAILLEUR_REF   = "bailleur-ref-123";
    private static final String LOCATAIRE_REF  = "locataire-ref-456";
    private static final int    PRIX_LOYER     = 500;
    private static final long   BAIL_ID        = 1L;

    private Client bailleur;
    private CampayTransaction campayTx;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(
                campayApiService,
                campayTransactionRepository,
                clientRepository,
                bailRepository,
                transactionRepository,
                paymentNotificationService
        );

        // Bailleur avec solde initial de 100 XAF
        bailleur = new Client();
        bailleur.setReference(BAILLEUR_REF);
        bailleur.setSolde(BigDecimal.valueOf(100));

        // Appart avec prixLoyer = 500
        Appart appart = new Appart();
        appart.setPrixLoyer(PRIX_LOYER);
        appart.setBailleur(bailleur);

        // Bail
        Bail bail = new Bail();
        bail.setId(BAIL_ID);
        bail.setAppart(appart);

        // Transaction Campay PENDING de type PAIEMENT_LOYER
        campayTx = new CampayTransaction();
        campayTx.setCampayReference(CAMPAY_REF);
        campayTx.setType(PaymentType.PAIEMENT_LOYER);
        campayTx.setStatus(PaymentStatus.PENDING);
        campayTx.setBailId(BAIL_ID);
        campayTx.setClientReference(LOCATAIRE_REF);
        campayTx.setBailleurReference(BAILLEUR_REF);

        // Mocks
        when(campayTransactionRepository.findByCampayReference(CAMPAY_REF))
                .thenReturn(Optional.of(campayTx));
        when(bailRepository.findById(BAIL_ID))
                .thenReturn(Optional.of(bail));
        when(clientRepository.findByReference(BAILLEUR_REF))
                .thenReturn(Optional.of(bailleur));
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(i -> i.getArgument(0));
        when(clientRepository.saveAndFlush(any(Client.class)))
                .thenAnswer(i -> i.getArgument(0));

        // Campay retourne SUCCESSFUL
        CampayStatusResponse statusOk = new CampayStatusResponse();
        statusOk.setStatus("SUCCESSFUL");
        statusOk.setReference(CAMPAY_REF);
        when(campayApiService.checkStatus(CAMPAY_REF)).thenReturn(statusOk);
    }

    @Test
    @DisplayName("verifierStatut SUCCESSFUL → solde bailleur incrémenté du montant du loyer")
    void soldeBailleur_incrementeDesPrixLoyer_apresConfirmation() throws Exception {
        paymentService.verifierStatut(CAMPAY_REF);

        // Le solde doit être 100 (initial) + 500 (loyer) = 600 XAF
        assertEquals(BigDecimal.valueOf(600), bailleur.getSolde(),
                "Le solde du bailleur doit être incrémenté du montant du loyer (500 XAF)");
    }

    @Test
    @DisplayName("verifierStatut SUCCESSFUL → une Transaction loyer est créée en base")
    void transactionLoyer_creee_apresConfirmation() throws Exception {
        paymentService.verifierStatut(CAMPAY_REF);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(captor.capture());
        Transaction txSaved = captor.getValue();
        assertEquals(PRIX_LOYER, txSaved.getMontant());
        assertEquals(CAMPAY_REF, txSaved.getCampayReference());
    }

    @Test
    @DisplayName("verifierStatut SUCCESSFUL → saveAndFlush appelé sur le bailleur")
    void saveAndFlush_appeleSurBailleur_apresConfirmation() throws Exception {
        paymentService.verifierStatut(CAMPAY_REF);

        verify(clientRepository).saveAndFlush(bailleur);
    }

    @Test
    @DisplayName("verifierStatut SUCCESSFUL → bailleurReference auto-corrigé si null")
    void bailleurReference_autoCorrige_siNull() throws Exception {
        campayTx.setBailleurReference(null); // Simule un champ null (ancien paiement)

        paymentService.verifierStatut(CAMPAY_REF);

        assertEquals(BAILLEUR_REF, campayTx.getBailleurReference(),
                "bailleurReference doit être auto-corrigé depuis bail.appart.bailleur");
        assertEquals(BigDecimal.valueOf(600), bailleur.getSolde());
    }
}
