package com.example.controllers;

import com.example.domain.dto.LogementDto;
import com.example.domain.entities.Client;
import com.example.domain.entities.Logement;
import com.example.domain.exceptions.NoClientFoundException;
import com.example.domain.exceptions.NoLogementFoundException;
import com.example.domain.exceptions.ValidationException;
import com.example.domain.mapper.LogementMapper;
import com.example.helper.ResponseHelper;
import com.example.services.impl.ClientService;
import com.example.services.impl.LogementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/users/{reference}/logements")
public class LogementController {

    @Autowired
    private LogementService logementService;

    @Autowired
    private ClientService clientService;

    @PostMapping("/create")
    public ResponseEntity<LogementDto> addNewLogement(@Valid @RequestBody LogementDto dto, Errors erros, @NotBlank @PathVariable("reference") String reference) throws Exception {
        ResponseHelper.handle(erros);
        Client bailleur = clientService.getClientByReference(reference);
        Logement logement = LogementMapper.getMapper().entitie(dto);
        logement.setClient(bailleur);
        logementService.register(logement);
        URI uri = URI.create("/users/" + bailleur.getReference() + "/logements/");
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping("")
    @Operation(description = "Get list of all logement by user")
    public ResponseEntity<List<LogementDto>> getAllLogementByUser(@NotBlank @PathVariable("reference") String reference) throws Exception {
        Client bailleur = clientService.getClientByReference(reference);
        List<LogementDto> dtoLogements = new ArrayList<>();
        logementService.getAllLogementByUser(bailleur).forEach(logement -> {
                    LogementDto dto = LogementMapper.getMapper().dto(logement);
                    dtoLogements.add(dto);
                }
        );
        return dtoLogements.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoLogements);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogementDto> getUserLogementById(
            @Parameter(description = "id of Logement")
            @NotBlank @PathVariable("id") Long id, @NotBlank @PathVariable("reference") String reference) throws NoLogementFoundException, NoClientFoundException {
        Client bailleur = clientService.getClientByReference(reference);
        LogementDto dto = LogementMapper.getMapper().dto(logementService.getUserLogementById(bailleur, id));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogementDto> updateLogementById(@RequestBody LogementDto logementDto, Errors erros,
                                                          @Parameter(description = "id of Logement")
                                                          @NotBlank @PathVariable("id") Long id, @NotBlank @PathVariable("reference") String reference) throws NoLogementFoundException, ValidationException {
        ResponseHelper.handle(erros);
        LogementDto dto = LogementMapper.getMapper().dto(logementService.updateLogementById(logementDto, id));
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteLogementById(@NotBlank @PathVariable("id") Long id) throws NoLogementFoundException {
        logementService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
