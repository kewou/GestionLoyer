package com.example.features.payment.service;

import com.example.features.bail.Bail;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.enums.PaymentStatus;
import com.example.features.payment.enums.PaymentType;
import com.example.features.user.domain.entities.Client;
import com.example.features.payment.repository.CampayTransactionRepository;
import com.example.features.bail.BailRepository;
import com.example.features.user.infra.ClientRepository;
import com.example.features.transaction.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PaymentServiceTest {
    @Mock
    private CampayTransactionRepository campayTransactionRepository;
    @Mock
    private BailRepository bailRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void traiterPaiementLoyer_creditSoldeBailleur() {
        // Préparation des entités
        Client bailleur = new Client();
        bailleur.setReference("bailleur-123");
        bailleur.setSolde(BigDecimal.valueOf(100));
        Appart appart = new Appart();
        appart.setPrixLoyer(500);
        appart.setBailleur(bailleur);
        Bail bail = new Bail();
        bail.setId(1L);
        bail.setAppart(appart);
        CampayTransaction tx = new CampayTransaction();
        tx.setCampayReference("ref-om-1");
        tx.setType(PaymentType.PAIEMENT_LOYER);
        tx.setStatus(PaymentStatus.SUCCESSFUL);
        tx.setBailId(1L);
        // Simule un bailleurReference null (pour tester la robustesse)
        tx.setBailleurReference(null);

        when(bailRepository.findById(1L)).thenReturn(Optional.of(bail));
        when(clientRepository.findByReference("bailleur-123")).thenReturn(Optional.of(bailleur));

        // Appel
        paymentService.traiterPaiementLoyer(tx);

        // Vérification : le solde du bailleur doit être incrémenté du montant du loyer
        assertEquals(BigDecimal.valueOf(600), bailleur.getSolde());
        // Le champ bailleurReference doit être corrigé
        assertEquals("bailleur-123", tx.getBailleurReference());
        // Vérifie que la transaction a bien été sauvegardée
        verify(clientRepository).saveAndFlush(bailleur);
    }
}

