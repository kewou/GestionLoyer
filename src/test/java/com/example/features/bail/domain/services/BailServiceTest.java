package com.example.features.bail.domain.services;

import com.example.exceptions.BusinessException;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.bail.Bail;
import com.example.features.bail.BailMapper;
import com.example.features.bail.BailRepository;
import com.example.features.bail.BailService;
import com.example.features.bail.dto.BailDto;
import com.example.features.bail.dto.CreateBailRequestDto;
import com.example.features.transaction.Transaction;
import com.example.features.transaction.TransactionMapper;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BailService Tests")
class BailServiceTest {

    @Mock
    private BailRepository bailRepository;

    @Mock
    private AppartRepository appartRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BailMapper bailMapper;

    @Mock
    private TransactionMapper transactionMapper;

    private BailService bailService;

    @BeforeEach
    void setUpService() throws Exception {
        // Créer l'instance avec le constructeur
        bailService = new BailService(bailMapper);

        // Injecter manuellement les mocks dans les champs @Autowired via reflection
        java.lang.reflect.Field bailRepoField = BailService.class.getDeclaredField("bailRepository");
        bailRepoField.setAccessible(true);
        bailRepoField.set(bailService, bailRepository);

        java.lang.reflect.Field appartRepoField = BailService.class.getDeclaredField("appartRepository");
        appartRepoField.setAccessible(true);
        appartRepoField.set(bailService, appartRepository);

        java.lang.reflect.Field clientRepoField = BailService.class.getDeclaredField("clientRepository");
        clientRepoField.setAccessible(true);
        clientRepoField.set(bailService, clientRepository);

        java.lang.reflect.Field txRepoField = BailService.class.getDeclaredField("transactionRepository");
        txRepoField.setAccessible(true);
        txRepoField.set(bailService, transactionRepository);

        java.lang.reflect.Field txMapperField = BailService.class.getDeclaredField("transactionMapper");
        txMapperField.setAccessible(true);
        txMapperField.set(bailService, transactionMapper);
    }

    @Test
    @DisplayName("Devrait assigner un locataire à un appartement via bail")
    void testAssignLocataire() throws BusinessException {
        // Given
        String refAppart = "APPART001";
        LocalDate dateEntree = LocalDate.now();

        Appart appart = new Appart();
        appart.setReference(refAppart);

        Client locataire = Client.builder()
                .reference("LOCATAIRE001")
                .name("Locataire")
                .build();

        CreateBailRequestDto request = new CreateBailRequestDto();
        request.setLocataireRef("LOCATAIRE001");
        request.setDateEntree(dateEntree);

        Bail savedBail = new Bail();
        savedBail.setId(1L);
        savedBail.setAppart(appart);
        savedBail.setLocataire(locataire);
        savedBail.setDateEntree(dateEntree);

        BailDto bailDto = BailDto.builder()
                .id(1L)
                .dateEntree(dateEntree)
                .build();

        when(appartRepository.findByReference(refAppart)).thenReturn(Optional.of(appart));
        when(bailRepository.findByAppartAndDateSortieIsNull(appart)).thenReturn(Optional.empty());
        when(clientRepository.findByReference("LOCATAIRE001")).thenReturn(Optional.of(locataire));
        when(bailRepository.save(any(Bail.class))).thenReturn(savedBail);
        when(bailMapper.dto(any(Bail.class))).thenReturn(bailDto);

        // When
        BailDto result = bailService.assignLocataire(refAppart, request);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(bailRepository, times(1)).save(any(Bail.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception si appartement introuvable")
    void testAssignLocataireAppartNotFound() {
        // Given
        String refAppart = "UNKNOWN_APPART";
        CreateBailRequestDto request = new CreateBailRequestDto();
        request.setLocataireRef("LOCATAIRE001");
        request.setDateEntree(LocalDate.now());

        when(appartRepository.findByReference(refAppart)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () ->
                bailService.assignLocataire(refAppart, request)
        );
        verify(bailRepository, never()).save(any(Bail.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception si locataire introuvable")
    void testAssignLocataireLocataireNotFound() {
        // Given
        String refAppart = "APPART001";
        Appart appart = new Appart();
        appart.setReference(refAppart);

        CreateBailRequestDto request = new CreateBailRequestDto();
        request.setLocataireRef("UNKNOWN_LOCATAIRE");
        request.setDateEntree(LocalDate.now());

        when(appartRepository.findByReference(refAppart)).thenReturn(Optional.of(appart));
        when(bailRepository.findByAppartAndDateSortieIsNull(appart)).thenReturn(Optional.empty());
        when(clientRepository.findByReference("UNKNOWN_LOCATAIRE")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(BusinessException.class, () ->
                bailService.assignLocataire(refAppart, request)
        );
        verify(bailRepository, never()).save(any(Bail.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception si bail actif déjà existant")
    void testAssignLocataireBailAlreadyActive() {
        // Given
        String refAppart = "APPART001";
        Appart appart = new Appart();
        appart.setReference(refAppart);

        Bail activeBail = new Bail();
        activeBail.setId(1L);

        CreateBailRequestDto request = new CreateBailRequestDto();
        request.setLocataireRef("LOCATAIRE001");
        request.setDateEntree(LocalDate.now());

        when(appartRepository.findByReference(refAppart)).thenReturn(Optional.of(appart));
        when(bailRepository.findByAppartAndDateSortieIsNull(appart)).thenReturn(Optional.of(activeBail));

        // When & Then
        assertThrows(BusinessException.class, () ->
                bailService.assignLocataire(refAppart, request)
        );
        verify(bailRepository, never()).save(any(Bail.class));
    }

    @Test
    @DisplayName("Devrait sortir un locataire d'un bail")
    void testSortirLocataire() {
        // Given
        Long bailId = 1L;
        Bail bail = new Bail();
        bail.setId(bailId);
        bail.setDateEntree(LocalDate.now().minusMonths(6));
        bail.setActif(true);

        BailDto bailDto = BailDto.builder()
                .id(bailId)
                .dateSortie(LocalDate.now())
                .actif(false)
                .build();

        when(bailRepository.findById(bailId)).thenReturn(Optional.of(bail));
        when(bailRepository.save(any(Bail.class))).thenReturn(bail);
        when(bailMapper.dto(any(Bail.class))).thenReturn(bailDto);

        // When
        BailDto result = bailService.sortirLocataire(bailId);

        // Then
        assertNotNull(result);
        assertFalse(result.getActif());
        verify(bailRepository, times(1)).save(any(Bail.class));
    }

    @Test
    @DisplayName("Devrait lancer une exception si bail introuvable à la sortie")
    void testSortirLocataireBailNotFound() {
        // Given
        Long bailId = 999L;

        when(bailRepository.findById(bailId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () ->
                bailService.sortirLocataire(bailId)
        );
    }

    @Test
    @DisplayName("Devrait récupérer le bail actuel d'un appartement")
    void testGetBailActuel() {
        // Given
        Appart appart = new Appart();
        appart.setReference("APPART001");

        Bail bail = new Bail();
        bail.setId(1L);
        bail.setAppart(appart);
        bail.setDateEntree(LocalDate.now());

        when(bailRepository.findByAppartAndDateSortieIsNull(appart)).thenReturn(Optional.of(bail));

        // When
        Optional<Bail> result = bailService.getBailActuel(appart);

        // Then
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    @DisplayName("Devrait retourner vide si pas de bail actuel")
    void testGetBailActuelEmpty() {
        // Given
        Appart appart = new Appart();
        appart.setReference("APPART001");

        when(bailRepository.findByAppartAndDateSortieIsNull(appart)).thenReturn(Optional.empty());

        // When
        Optional<Bail> result = bailService.getBailActuel(appart);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Devrait récupérer les transactions d'un bail")
    void testGetTransactions() {
        // Given
        Long bailId = 1L;
        Bail bail = new Bail();
        bail.setId(bailId);

        Transaction tx = new Transaction();
        tx.setId(1L);
        tx.setMontant(500);
        tx.setBail(bail);

        when(bailRepository.findById(bailId)).thenReturn(Optional.of(bail));
        when(transactionRepository.findByBail(bail)).thenReturn(List.of(tx));

        // When
        var result = bailService.getTransactions(bailId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}


