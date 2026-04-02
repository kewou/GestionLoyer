package com.example.features.transaction.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.transaction.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TransactionService Tests")
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AppartAppService appartAppService;

    @Mock
    private TransactionMapper transactionMapper;

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService(transactionRepository, appartAppService, transactionMapper);
    }

    @Test
    @DisplayName("Devrait retourner toutes les transactions d'un appartement")
    void testGetAllTransactionByAppart() throws BusinessException {
        // Given
        String refAppart = "APPART001";
        Appart appart = new Appart();
        appart.setReference(refAppart);

        Transaction tx1 = new Transaction();
        tx1.setId(1L);
        tx1.setMontant(500);
        tx1.setDate(LocalDate.now());

        Transaction tx2 = new Transaction();
        tx2.setId(2L);
        tx2.setMontant(300);
        tx2.setDate(LocalDate.now().minusMonths(1));

        TransactionDto dto1 = TransactionDto.builder().montant(500).date(LocalDate.now()).build();
        TransactionDto dto2 = TransactionDto.builder().montant(300).date(LocalDate.now().minusMonths(1)).build();

        when(appartAppService.getAppartFromDatabase(refAppart)).thenReturn(appart);
        when(transactionRepository.findByBail_Appart(appart)).thenReturn(List.of(tx1, tx2));
        when(transactionMapper.dto(tx1)).thenReturn(dto1);
        when(transactionMapper.dto(tx2)).thenReturn(dto2);

        // When
        List<TransactionDto> result = transactionService.getAllTransactionByAppart(refAppart);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(500, result.get(0).getMontant());
        assertEquals(300, result.get(1).getMontant());
        verify(appartAppService, times(1)).getAppartFromDatabase(refAppart);
    }

    @Test
    @DisplayName("Devrait retourner une liste vide si aucune transaction")
    void testGetAllTransactionByAppartEmpty() throws BusinessException {
        // Given
        String refAppart = "APPART001";
        Appart appart = new Appart();
        appart.setReference(refAppart);

        when(appartAppService.getAppartFromDatabase(refAppart)).thenReturn(appart);
        when(transactionRepository.findByBail_Appart(appart)).thenReturn(new ArrayList<>());

        // When
        List<TransactionDto> result = transactionService.getAllTransactionByAppart(refAppart);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Devrait lancer une exception si appartement introuvable")
    void testGetAllTransactionByAppartNotFound() throws BusinessException {
        // Given
        String refAppart = "UNKNOWN_APPART";

        when(appartAppService.getAppartFromDatabase(refAppart))
                .thenThrow(new BusinessException("Appart not found"));

        // When & Then
        assertThrows(BusinessException.class, () ->
                transactionService.getAllTransactionByAppart(refAppart)
        );
    }
}
