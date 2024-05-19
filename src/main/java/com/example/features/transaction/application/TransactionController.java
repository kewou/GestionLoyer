package com.example.features.transaction.application;

import com.example.exceptions.BusinessException;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.domain.services.AppartService;
import com.example.features.logement.domain.services.LogementService;
import com.example.features.transaction.application.appService.TransactionAppService;
import com.example.features.transaction.application.mapper.TransactionDto;
import com.example.features.transaction.application.mapper.TransactionMapper;
import com.example.features.transaction.domain.entities.Transaction;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.helper.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.URI;

@RestController
@RequestMapping("/users/{reference}/apparts")
public class TransactionController {


    private TransactionAppService transactionAppService;

    @Autowired
    public TransactionController(TransactionAppService transactionAppService) {
        this.transactionAppService = transactionAppService;
    }

    @Autowired
    private LogementService logementService;

    @Autowired
    private AppartService appartService;

    @Autowired
    private ClientService clientService;

    /*
    @GetMapping("")
    public ResponseEntity<List<AppartDto>> getAllAppartUser(@NotBlank @PathVariable("reference") String reference) throws BusinessException {
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
    }*/

    @PostMapping("/{refAppart}/nouvelle-transaction")
    public ResponseEntity<TransactionDto> addNewTransaction(@Valid @RequestBody TransactionDto dto, Errors erros,
                                                            @NotBlank @PathVariable("reference") String reference,
                                                            @NotBlank @PathVariable("refAppart") String refAppart) throws BusinessException {
        ResponseHelper.handle(erros);
        Client bailleur = clientService.getClientByReference(reference);
        Appart appart = appartService.getAppartByRef(refAppart);
        Transaction transaction = TransactionMapper.getMapper().entitie(dto);
        TransactionDto dtoRetour = TransactionMapper.getMapper().dto(transactionAppService.register(bailleur, transaction, appart));
        URI uri = URI.create("/users/" + "/ref/" + "/apparts");

        return ResponseEntity.created(uri).body(dtoRetour);
    }


}
