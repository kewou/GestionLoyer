package com.example.controllers;

import com.example.domain.dto.AppartDto;
import com.example.domain.dto.TransactionDto;
import com.example.domain.entities.Appart;
import com.example.domain.entities.Client;
import com.example.domain.entities.Transaction;
import com.example.domain.exceptions.BusinessException;
import com.example.domain.exceptions.ValidationException;
import com.example.domain.mapper.AppartMapper;
import com.example.domain.mapper.TransactionMapper;
import com.example.helper.ResponseHelper;
import com.example.services.impl.AppartService;
import com.example.services.impl.ClientService;
import com.example.services.impl.LogementService;
import com.example.services.impl.TransactionService;
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
@RequestMapping("/users/{reference}/apparts")
public class TransactionController {

    @Autowired
    private LogementService logementService;

    @Autowired
    private AppartService appartService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("")
    public ResponseEntity<List<AppartDto>> getAllAppart(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
        Client bailleur = clientService.getClientByReference(reference);
        List<AppartDto> dtoApparts = new ArrayList<>();
        logementService.getAllLogementByUser(bailleur).forEach(logement -> appartService.getAllAppartByLogement(logement).forEach(appart -> {
                    AppartDto dto = AppartMapper.getMapper().dto(appart);
                    dtoApparts.add(dto);
                })
        );

        return dtoApparts.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(dtoApparts);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<List<AppartDto>> getSuggestions(@RequestParam String term) {
        List<AppartDto> suggestions = appartService.rechercherSuggestionsParNom(term);
        return ResponseEntity.ok().body(suggestions);
    }

    @PostMapping("/{idAppart}/nouvelle-transaction")
    public ResponseEntity<TransactionDto> addNewTransaction(@Valid @RequestBody TransactionDto dto, Errors erros,
                                                            @NotBlank @PathVariable("reference") String reference,
                                                            @NotBlank @PathVariable("idAppart") Long idAppart) throws BusinessException {
        ResponseHelper.handle(erros);
        Client bailleur = clientService.getClientByReference(reference);
        Appart appart = appartService.getAppartById(idAppart);
        Transaction transaction = TransactionMapper.getMapper().entitie(dto);
        TransactionDto dtoRetour = TransactionMapper.getMapper().dto(transactionService.register(bailleur, transaction, appart));
        URI uri = URI.create("/users/" + "/ref/" + "/apparts");

        return ResponseEntity.created(uri).body(dtoRetour);
    }

    @PatchMapping("/{idAppart}/nouveau-locataire/{referenceLocataire}")
    public ResponseEntity<AppartDto> updateAppartAssigneLocataire(
            @NotBlank @PathVariable("referenceLocataire") String referenceLocataire,
            @NotBlank @PathVariable("idAppart") Long idAppart) throws ValidationException, BusinessException {
        Client locataire = clientService.getClientByReference(referenceLocataire);
        AppartDto dto = AppartMapper.getMapper().dto(appartService.updateAppartAssigneLocataire(idAppart, locataire));
        return ResponseEntity.ok(dto);
    }


}
