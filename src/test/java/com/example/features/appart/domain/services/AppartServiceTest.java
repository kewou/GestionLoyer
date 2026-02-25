package com.example.features.appart.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.application.mapper.AppartMapper;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.bail.BailMapper;
import com.example.features.bail.BailRepository;
import com.example.features.logement.Logement;
import com.example.features.logement.LogementAppService;
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
@DisplayName("AppartService Tests")
class AppartServiceTest {

    @Mock
    private LogementAppService logementAppService;

    @Mock
    private AppartRepository appartRepository;

    @Mock
    private BailRepository bailRepository;

    @Mock
    private BailMapper bailMapper;

    @Mock
    private AppartMapper appartMapper;

    private AppartService appartService;

    @BeforeEach
    void setUp() {
        appartService = new AppartService(logementAppService, appartRepository, bailRepository, bailMapper, appartMapper);
    }

    private Logement createLogement(String reference) {
        Logement l = new Logement();
        l.setReference(reference);
        Client client = Client.builder().reference("USER123").build();
        l.setClient(client);
        return l;
    }

    private Appart createAppart(String reference, String nom, Integer prixLoyer) {
        Appart a = new Appart();
        a.setReference(reference);
        a.setNom(nom);
        a.setPrixLoyer(prixLoyer);
        return a;
    }

    @Test
    @DisplayName("Devrait créer un appartement avec succès")
    void testRegisterAppart() throws BusinessException {
        // Given
        String logementRef = "LOGEMENT001";
        AppartDto appartDto = AppartDto.builder()
                .reference("APPART001").nom("Apt 102").prixLoyer(500).prixCaution(200).build();

        Logement logement = createLogement(logementRef);
        Appart appart = createAppart("APPART001", "Apt 102", 500);
        AppartDto resultDto = AppartDto.builder()
                .reference("APPART001").nom("Apt 102").prixLoyer(500).prixCaution(200).build();

        when(logementAppService.getLogementFromDatabase(logementRef)).thenReturn(logement);
        when(appartMapper.entitie(any(AppartDto.class))).thenReturn(appart);
        when(appartRepository.save(any(Appart.class))).thenReturn(appart);
        when(appartMapper.dto(any(Appart.class))).thenReturn(resultDto);

        // When
        AppartDto result = appartService.register(logementRef, appartDto);

        // Then
        assertNotNull(result);
        assertEquals("APPART001", result.getReference());
        assertEquals(500, result.getPrixLoyer());
        verify(appartRepository, times(1)).save(any(Appart.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception si logement introuvable")
    void testRegisterAppartLogementNotFound() throws BusinessException {
        // Given
        String logementRef = "UNKNOWN_LOGEMENT";
        AppartDto appartDto = AppartDto.builder().reference("APPART001").nom("Apt 102").build();

        when(logementAppService.getLogementFromDatabase(logementRef))
                .thenThrow(new BusinessException("Logement not found"));

        // When & Then
        assertThrows(BusinessException.class, () -> appartService.register(logementRef, appartDto));
        verify(appartRepository, never()).save(any(Appart.class));
    }

    @Test
    @DisplayName("Devrait récupérer un appartement par référence logement et appart")
    void testGetLogementApprtByRef() throws BusinessException {
        // Given
        String logementRef = "LOGEMENT001";
        String appartRef = "APPART001";

        Logement logement = createLogement(logementRef);
        Appart appart = createAppart(appartRef, "Apt 102", 500);
        appart.setLogement(logement);
        AppartDto appartDto = AppartDto.builder().reference(appartRef).nom("Apt 102").build();

        when(logementAppService.getLogementFromDatabase(logementRef)).thenReturn(logement);
        when(appartRepository.findByLogementAndReference(logement, appartRef)).thenReturn(Optional.of(appart));
        when(appartMapper.dto(any(Appart.class))).thenReturn(appartDto);

        // When
        AppartDto result = appartService.getLogementApprtByRef(logementRef, appartRef);

        // Then
        assertNotNull(result);
        assertEquals(appartRef, result.getReference());
    }

    @Test
    @DisplayName("Devrait lancer une exception si appartement introuvable")
    void testGetLogementApprtByRefNotFound() throws BusinessException {
        // Given
        String logementRef = "LOGEMENT001";
        String appartRef = "UNKNOWN_APPART";

        Logement logement = createLogement(logementRef);

        when(logementAppService.getLogementFromDatabase(logementRef)).thenReturn(logement);
        when(appartRepository.findByLogementAndReference(logement, appartRef)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> appartService.getLogementApprtByRef(logementRef, appartRef));
    }

    @Test
    @DisplayName("Devrait retourner tous les appartements d'un logement")
    void testGetAllAppartByLogement() throws BusinessException {
        // Given
        String logementRef = "LOGEMENT001";
        Logement logement = createLogement(logementRef);

        Appart appart1 = createAppart("APPART001", "Apt 101", 400);
        Appart appart2 = createAppart("APPART002", "Apt 102", 500);
        AppartDto dto1 = AppartDto.builder().reference("APPART001").nom("Apt 101").build();
        AppartDto dto2 = AppartDto.builder().reference("APPART002").nom("Apt 102").build();

        when(logementAppService.getLogementFromDatabase(logementRef)).thenReturn(logement);
        when(appartRepository.findByLogement(logement)).thenReturn(List.of(appart1, appart2));
        when(appartMapper.dto(appart1)).thenReturn(dto1);
        when(appartMapper.dto(appart2)).thenReturn(dto2);

        // When
        List<AppartDto> result = appartService.getAllAppartByLogement(logementRef);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Devrait mettre à jour un appartement")
    void testUpdateLogementByRef() throws BusinessException {
        // Given
        String appartRef = "APPART001";

        Appart existingAppart = createAppart(appartRef, "Apt 101", 500);
        Appart updatedAppart = createAppart(appartRef, "Apt 102", 600);
        AppartDto updateDto = AppartDto.builder().nom("Apt 102").prixLoyer(600).build();
        AppartDto resultDto = AppartDto.builder().reference(appartRef).nom("Apt 102").prixLoyer(600).build();

        when(appartRepository.findByReference(appartRef)).thenReturn(Optional.of(existingAppart));
        when(appartMapper.entitie(any(AppartDto.class))).thenReturn(updatedAppart);
        when(appartRepository.save(any(Appart.class))).thenReturn(existingAppart);
        when(appartMapper.dto(any(Appart.class))).thenReturn(resultDto);

        // When
        AppartDto result = appartService.updateLogementByRef(updateDto, appartRef);

        // Then
        assertNotNull(result);
        assertEquals("Apt 102", result.getNom());
        assertEquals(600, result.getPrixLoyer());
        verify(appartRepository, times(1)).save(any(Appart.class));
    }

    @Test
    @DisplayName("Devrait supprimer un appartement")
    void testDeleteByRef() throws BusinessException {
        // Given
        String appartRef = "APPART001";
        Appart appart = createAppart(appartRef, "Apt 101", 500);

        when(appartRepository.findByReference(appartRef)).thenReturn(Optional.of(appart));

        // When
        appartService.deleteByRef(appartRef);

        // Then
        verify(appartRepository, times(1)).deleteByReference(appartRef);
    }

    @Test
    @DisplayName("Devrait lancer une exception à la suppression si appart introuvable")
    void testDeleteByRefNotFound() {
        // Given
        String appartRef = "UNKNOWN_APPART";

        when(appartRepository.findByReference(appartRef)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () -> appartService.deleteByRef(appartRef));
        verify(appartRepository, never()).deleteByReference(anyString());
    }
}
