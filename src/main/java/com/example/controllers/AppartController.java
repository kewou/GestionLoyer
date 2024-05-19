package com.example.controllers;

import com.example.domain.dto.AppartDto;
import com.example.domain.entities.Appart;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.BusinessException;
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
@RequestMapping("/users/{reference}/logements/{refLgt}/apparts")
public class AppartController {

    @Autowired
    private LogementService logementService;

    @Autowired
    private AppartService appartService;

    @Autowired
    private ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<AppartDto> addNewAppartement(@Valid @RequestBody AppartDto dto, Errors erros,
                                                       @NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        ResponseHelper.handle(erros);
        Logement logement = logementService.getLogementByReference(refLgt);
        Appart appart = AppartMapper.getMapper().entitie(dto);
        appart.setLogement(logement);
        appart.setBailleur(logement.getClient());
        appartService.register(appart);
        URI uri = URI.create("/users/" + "/logements/" + refLgt + "/apparts");
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("")
    @Operation(description = "Get list of all appart by logement")
    public ResponseEntity<List<AppartDto>> getAllAppartByLogement(@NotBlank @PathVariable("refLgt") String refLgt) throws BusinessException {
        Logement logement = logementService.getLogementByReference(refLgt);
        List<AppartDto> dtoApparts = new ArrayList<>();
        appartService.getAllAppartByLogement(logement).forEach(appart -> {
                    AppartDto dto = AppartMapper.getMapper().dto(appart);
                    dtoApparts.add(dto);
                }
        );
        return dtoApparts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoApparts);
    }

    @GetMapping("/{refAppart}")
    public ResponseEntity<AppartDto> getAppartLogementByRef(
            @NotBlank @PathVariable("refLgt") String refLgt,
            @NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        Logement logement = logementService.getLogementByReference(refLgt);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.getLogementApprtByRef(logement, refAppart));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{refAppart}")
    public ResponseEntity<AppartDto> updateAppartLogementByRef(
            @RequestBody AppartDto appartDto,
            Errors erros,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        ResponseHelper.handle(erros);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateLogementByRef(appartDto, refAppart));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(path = "/{refAppart}")
    public ResponseEntity<Void> deleteAppartById(@NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        appartService.deleteByRef(refAppart);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{refAppart}/nouveau-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartAssigneLocataire(
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        Client locataire = clientService.getClientByReference(referenceLocataire);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateAppartAssigneLocataire(refAppart, locataire));
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{refAppart}/sortir-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartSortLocataire(
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("refAppart") String refAppart) throws ValidationException, BusinessException {
        Client locataire = clientService.getClientByReference(referenceLocataire);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateAppartSortirLocataire(refAppart));
        return ResponseEntity.ok(dto);
    }


}
