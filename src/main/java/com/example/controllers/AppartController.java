package com.example.controllers;

import com.example.domain.dto.AppartDto;
import com.example.domain.entities.Appart;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.NoAppartFoundException;
import com.example.domain.exceptions.NoClientFoundException;
import com.example.domain.exceptions.NoLogementFoundException;
import com.example.domain.exceptions.ValidationException;
import com.example.domain.mapper.AppartMapper;
import com.example.helper.ResponseHelper;
import com.example.services.impl.AppartService;
import com.example.services.impl.ClientService;
import com.example.services.impl.LogementService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users/{reference}/logements/{id}/apparts")
public class AppartController {

    @Autowired
    private LogementService logementService;

    @Autowired
    private AppartService appartService;

    @Autowired
    private ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<AppartDto> addNewAppartement(@Valid @RequestBody AppartDto dto, Errors erros, @NotBlank @PathVariable("id") Long idLogement) throws NoLogementFoundException {
        ResponseHelper.handle(erros);
        Logement logement = logementService.getLogementById(idLogement);
        Appart appart = AppartMapper.getMapper().entitie(dto);
        appart.setLogement(logement);
        appart.setBailleur(logement.getClient());
        appartService.register(appart);
        URI uri = URI.create("/users/" + "/logements/" + idLogement + "/apparts");
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("")
    @Operation(description = "Get list of all appart by logement")
    public ResponseEntity<List<AppartDto>> getAllAppartByLogement(@NotBlank @PathVariable("id") Long idLogement) throws Exception {
        Logement logement = logementService.getLogementById(idLogement);
        List<AppartDto> dtoApparts = new ArrayList<>();
        appartService.getAllAppartByLogement(logement).forEach(appart -> {
                    AppartDto dto = AppartMapper.getMapper().dto(appart);
                    dtoApparts.add(dto);
                }
        );
        return dtoApparts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoApparts);
    }

    @GetMapping("/{idAppart}")
    public ResponseEntity<AppartDto> getAppartLogementById(
            @NotBlank @PathVariable("id") Long idLogement,
            @NotBlank @PathVariable("idAppart") Long idAppart) throws NoLogementFoundException, NoAppartFoundException {
        Logement logement = logementService.getLogementById(idLogement);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.getLogementApprtById(logement, idAppart));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{idAppart}")
    public ResponseEntity<AppartDto> updateAppartLogementById(
            @RequestBody AppartDto appartDto,
            Errors erros,
            @NotBlank @PathVariable("idAppart") Long idAppart) throws NoAppartFoundException, ValidationException {
        ResponseHelper.handle(erros);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateAppartById(appartDto, idAppart));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{idAppart}")
    public ResponseEntity<Void> deleteAppartById(@NotBlank @PathVariable("idAppart") Long idAppart) throws NoAppartFoundException {
        appartService.deleteById(idAppart);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idAppart}/nouveau-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartAssigneLocataire(
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("idAppart") Long idAppart) throws NoClientFoundException, NoAppartFoundException, ValidationException {
        Client locataire = clientService.getClientByReference(referenceLocataire);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateAppartAssigneLocataire(idAppart, locataire));
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{idAppart}/sortir-locataire")
    public ResponseEntity<AppartDto> updateAppartSortirLocataire(
            @NotBlank @PathVariable("idAppart") Long idAppart) throws NoAppartFoundException, ValidationException {
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateAppartSortirLocataire(idAppart));
        return ResponseEntity.ok(dto);
    }


}
