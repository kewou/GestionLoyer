package com.example.features.user.application;

import com.example.exceptions.BusinessException;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.bail.Bail;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.utils.JWTUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contrôleur pour les opérations spécifiques aux locataires
 *
 * @author Joel NOUMIA
 */
@RestController
@Slf4j
@Transactional
@RequestMapping("/locataires")
public class LocataireController {

    private final ClientService clientService;
    private final JWTUtils jwtUtils;

    @Autowired
    public LocataireController(ClientService clientService, JWTUtils jwtUtils) {
        this.clientService = clientService;
        this.jwtUtils = jwtUtils;
    }

    @Operation(summary = "Récupère les informations d'un locataire", description = "Retourne les informations complètes d'un locataire avec ses appartements")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations du locataire récupérées avec succès", content = @Content(schema = @Schema(implementation = LocataireInfoDto.class))),
            @ApiResponse(responseCode = "404", description = "Locataire non trouvé", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    @GetMapping("/{reference}")
    @PreAuthorize("hasAuthority('LOCATAIRE')")
    public ResponseEntity<LocataireInfoDto> getLocataireInfo(
            @Parameter(description = "Référence du locataire") @NotBlank @PathVariable("reference") String reference,
            Authentication authentication)
            throws BusinessException {

        log.info("Récupération des informations pour le locataire: {}", reference);

        Client locataire = clientService.getClientWithBaux(reference);
        if (locataire == null) {
            log.warn("Locataire non trouvé: {}", reference);
            return ResponseEntity.notFound().build();
        }

        LocataireInfoDto locataireInfo = new LocataireInfoDto();
        locataireInfo.setReference(locataire.getReference());
        locataireInfo.setName(locataire.getName() + " " + locataire.getLastName());
        locataireInfo.setEmail(locataire.getEmail());
        locataireInfo.setPhone(locataire.getPhone());

        // Récupérer les appartements du locataire via les baux actifs
        List<LocataireAppartementDto> appartements = locataire.getBauxActifs()
                .stream()
                .map(this::mapBailToAppartementDto)
                .collect(Collectors.toList());

        locataireInfo.setAppartements(appartements);

        log.info("Informations récupérées pour le locataire {}: {} appartement(s)",
                reference, appartements.size());

        return ResponseEntity.ok(locataireInfo);
    }

    @Operation(summary = "Récupère les appartements d'un locataire", description = "Retourne la liste des appartements assignés à un locataire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste des appartements récupérée avec succès"),
            @ApiResponse(responseCode = "404", description = "Locataire non trouvé", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    @GetMapping("/{reference}/appartements")
    @PreAuthorize("hasAuthority('LOCATAIRE')")
    public ResponseEntity<List<LocataireAppartementDto>> getLocataireAppartements(
            @Parameter(description = "Référence du locataire") @NotBlank @PathVariable("reference") String reference,
            Authentication authentication)
            throws BusinessException {

        log.info("Récupération des appartements pour le locataire: {}", reference);

        Client locataire = clientService.getClientWithBaux(reference);
        if (locataire == null) {
            log.warn("Locataire non trouvé: {}", reference);
            return ResponseEntity.notFound().build();
        }

        List<LocataireAppartementDto> appartements = locataire.getBauxActifs()
                .stream()
                .map(this::mapBailToAppartementDto)
                .collect(Collectors.toList());

        log.info("{} appartement(s) trouvé(s) pour le locataire {}", appartements.size(), reference);

        return ResponseEntity.ok(appartements);
    }

    /**
     * Mappe un bail vers un DTO d'appartement pour locataire
     */
    private LocataireAppartementDto mapBailToAppartementDto(Bail bail) {
        Appart appart = bail.getAppart();

        LocataireAppartementDto dto = new LocataireAppartementDto();
        dto.setReference(appart.getReference());
        dto.setNom(appart.getNom());
        dto.setPrixLoyer(appart.getPrixLoyer());
        dto.setPrixCaution(appart.getPrixCaution());

        // Informations du logement
        LogementInfoDto logementInfo = new LogementInfoDto();
        logementInfo.setReference(appart.getLogement().getReference());
        logementInfo.setDescription(appart.getLogement().getDescription());
        logementInfo.setAdresse(appart.getLogement().getAdresse());
        dto.setLogement(logementInfo);

        // Informations du bail
        BailInfoDto bailInfo = new BailInfoDto();
        bailInfo.setId(bail.getId());
        bailInfo.setDateEntree(bail.getDateEntree());
        bailInfo.setDateSortie(bail.getDateSortie());
        bailInfo.setMontantLoyer(appart.getPrixLoyer());
        bailInfo.setMontantCaution(appart.getPrixCaution());
        bailInfo.setActif(bail.getActif());
        dto.setBail(bailInfo);

        return dto;
    }

    // DTOs pour la réponse
    public static class LocataireInfoDto {
        private String reference;
        private String name;
        private String email;
        private String phone;
        private List<LocataireAppartementDto> appartements;

        // Getters et setters
        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public List<LocataireAppartementDto> getAppartements() {
            return appartements;
        }

        public void setAppartements(List<LocataireAppartementDto> appartements) {
            this.appartements = appartements;
        }
    }

    public static class LocataireAppartementDto {
        private String reference;
        private String nom;
        private Integer prixLoyer;
        private Integer prixCaution;
        private LogementInfoDto logement;
        private BailInfoDto bail;

        // Getters et setters
        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public Integer getPrixLoyer() {
            return prixLoyer;
        }

        public void setPrixLoyer(Integer prixLoyer) {
            this.prixLoyer = prixLoyer;
        }

        public Integer getPrixCaution() {
            return prixCaution;
        }

        public void setPrixCaution(Integer prixCaution) {
            this.prixCaution = prixCaution;
        }

        public LogementInfoDto getLogement() {
            return logement;
        }

        public void setLogement(LogementInfoDto logement) {
            this.logement = logement;
        }

        public BailInfoDto getBail() {
            return bail;
        }

        public void setBail(BailInfoDto bail) {
            this.bail = bail;
        }
    }

    public static class LogementInfoDto {
        private String reference;
        private String description;
        private String adresse;

        // Getters et setters
        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }
    }

    public static class BailInfoDto {
        private Long id;
        private java.time.LocalDate dateEntree;
        private java.time.LocalDate dateSortie;
        private Integer montantLoyer;
        private Integer montantCaution;
        private Boolean actif;

        // Getters et setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public java.time.LocalDate getDateEntree() {
            return dateEntree;
        }

        public void setDateEntree(java.time.LocalDate dateEntree) {
            this.dateEntree = dateEntree;
        }

        public java.time.LocalDate getDateSortie() {
            return dateSortie;
        }

        public void setDateSortie(java.time.LocalDate dateSortie) {
            this.dateSortie = dateSortie;
        }

        public Integer getMontantLoyer() {
            return montantLoyer;
        }

        public void setMontantLoyer(Integer montantLoyer) {
            this.montantLoyer = montantLoyer;
        }

        public Integer getMontantCaution() {
            return montantCaution;
        }

        public void setMontantCaution(Integer montantCaution) {
            this.montantCaution = montantCaution;
        }

        public Boolean getActif() {
            return actif;
        }

        public void setActif(Boolean actif) {
            this.actif = actif;
        }
    }
}
