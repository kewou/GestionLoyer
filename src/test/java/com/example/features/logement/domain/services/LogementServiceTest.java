package com.example.features.logement.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.logement.Logement;
import com.example.features.logement.LogementDto;
import com.example.features.logement.LogementRepository;
import com.example.features.logement.LogementService;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LogementService Tests")
class LogementServiceTest {

    @Mock
    private LogementRepository logementRepository;

    @Mock
    private ClientAppService clientAppService;

    private LogementService logementService;

    @BeforeEach
    void setUp() {
        logementService = new LogementService(logementRepository, clientAppService);
    }

    private Logement createLogement(String reference, String quartier, Client client) {
        Logement l = new Logement();
        l.setReference(reference);
        l.setQuartier(quartier);
        l.setClient(client);
        return l;
    }

    @Test
    @DisplayName("Devrait créer un logement avec succès")
    void testRegisterLogement() throws BusinessException {
        // Given
        String userReference = "USER123";
        LogementDto logementDto = LogementDto.builder()
                .reference("LOGEMENT001")
                .quartier("Nkomkana")
                .ville("Yaounde")
                .description("Immeuble moderne")
                .build();

        Client client = Client.builder()
                .reference(userReference)
                .name("John")
                .lastName("Doe")
                .build();

        when(clientAppService.getClientFromDatabase(userReference)).thenReturn(client);
        when(logementRepository.save(any(Logement.class))).thenAnswer(i -> i.getArgument(0));

        // When
        LogementDto result = logementService.register(userReference, logementDto);

        // Then
        assertNotNull(result);
        verify(logementRepository, times(1)).save(any(Logement.class));
        verify(clientAppService, times(1)).getClientFromDatabase(userReference);
    }

    @Test
    @DisplayName("Devrait lancer une exception si utilisateur introuvable à la création")
    void testRegisterLogementUserNotFound() throws BusinessException {
        // Given
        String userReference = "UNKNOWN_USER";
        LogementDto logementDto = LogementDto.builder().reference("LOGEMENT001").build();

        when(clientAppService.getClientFromDatabase(userReference))
                .thenThrow(new BusinessException("User not found"));

        // When & Then
        assertThrows(BusinessException.class, () ->
                logementService.register(userReference, logementDto)
        );
        verify(logementRepository, never()).save(any(Logement.class));
    }

    @Test
    @DisplayName("Devrait récupérer un logement par référence utilisateur et logement")
    void testGetUserLogementByRef() throws BusinessException {
        // Given
        String userReference = "USER123";
        String logementReference = "LOGEMENT001";

        Client client = Client.builder().reference(userReference).build();
        Logement logement = createLogement(logementReference, "Nkomkana", client);

        when(clientAppService.getClientFromDatabase(userReference)).thenReturn(client);
        when(logementRepository.findByClientAndReference(client, logementReference))
                .thenReturn(Optional.of(logement));

        // When
        LogementDto result = logementService.getUserLogementByRef(userReference, logementReference);

        // Then
        assertNotNull(result);
        assertEquals(logementReference, result.getReference());
    }

    @Test
    @DisplayName("Devrait lancer une exception si logement introuvable")
    void testGetUserLogementByRefNotFound() throws BusinessException {
        // Given
        String userReference = "USER123";
        String logementReference = "UNKNOWN_LGT";

        Client client = Client.builder().reference(userReference).build();

        when(clientAppService.getClientFromDatabase(userReference)).thenReturn(client);
        when(logementRepository.findByClientAndReference(client, logementReference))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () ->
                logementService.getUserLogementByRef(userReference, logementReference)
        );
    }

    @Test
    @DisplayName("Devrait retourner tous les logements d'un utilisateur")
    void testGetAllLogementByUser() throws BusinessException {
        // Given
        String userReference = "USER123";
        Client client = Client.builder().reference(userReference).build();

        Logement lgt1 = createLogement("LOG001", "Q1", client);
        Logement lgt2 = createLogement("LOG002", "Q2", client);

        when(clientAppService.getClientFromDatabase(userReference)).thenReturn(client);
        when(logementRepository.findByClient(client)).thenReturn(List.of(lgt1, lgt2));

        // When
        List<LogementDto> result = logementService.getAllLogementByUser(userReference);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Devrait mettre à jour un logement")
    void testUpdateLogementByReference() throws BusinessException {
        // Given
        String logementReference = "LOGEMENT001";

        Logement existingLogement = createLogement(logementReference, "OldQuartier", null);

        LogementDto updateDto = LogementDto.builder()
                .quartier("NewQuartier")
                .description("New Description")
                .build();

        when(logementRepository.findByReference(logementReference))
                .thenReturn(Optional.of(existingLogement));
        when(logementRepository.save(any(Logement.class))).thenAnswer(i -> i.getArgument(0));

        // When
        LogementDto result = logementService.updateLogementByReference(updateDto, logementReference);

        // Then
        assertNotNull(result);
        verify(logementRepository, times(1)).save(any(Logement.class));
    }

    @Test
    @DisplayName("Devrait supprimer un logement")
    void testDeleteByReference() throws BusinessException {
        // Given
        String logementReference = "LOGEMENT001";
        Logement logement = createLogement(logementReference, "Quartier", null);

        when(logementRepository.findByReference(logementReference))
                .thenReturn(Optional.of(logement));

        // When
        logementService.deleteByReference(logementReference);

        // Then
        verify(logementRepository, times(1)).deleteByReference(logementReference);
    }

    @Test
    @DisplayName("Devrait lancer une exception à la suppression si logement introuvable")
    void testDeleteByReferenceNotFound() {
        // Given
        String logementReference = "UNKNOWN_LGT";

        when(logementRepository.findByReference(logementReference))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () ->
                logementService.deleteByReference(logementReference)
        );
        verify(logementRepository, never()).deleteByReference(anyString());
    }
}
