package com.example.features.bail;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.mapper.LoyerDto;
import com.example.features.bail.dto.BailDto;
import com.example.features.bail.dto.CreateBailRequestDto;
import com.example.features.transaction.TransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/baux")
public class BailController {

    @Autowired
    private BailService bailService;

    // no need for TransactionService here

    @PostMapping("/apparts/{refAppart}")
    @PreAuthorize("hasAuthority('BAILLEUR') or hasAuthority('ADMIN')")
    public ResponseEntity<BailDto> assignLocataire(
            @PathVariable String refAppart,
            @Valid @RequestBody CreateBailRequestDto request) throws BusinessException {
        BailDto bail = bailService.assignLocataire(refAppart, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(bail);
    }

    @GetMapping("/{bailId}/historique-loyers")
    public ResponseEntity<List<LoyerDto>> getHistoriqueLoyers(@PathVariable Long bailId) {
        List<LoyerDto> loyers = bailService.getHistoriqueLoyers(bailId);
        return ResponseEntity.ok(loyers);
    }

    @PostMapping("/{bailId}/transactions")
    public ResponseEntity<List<LoyerDto>> createTransaction(
            @PathVariable Long bailId,
            @RequestBody TransactionDto request) {

        List<LoyerDto> loyers = bailService.createTransaction(bailId, request.getMontant());
        return ResponseEntity.ok(loyers);
    }

    @GetMapping("/{bailId}/transactions")
    public ResponseEntity<List<TransactionDto>> listTransactions(@PathVariable Long bailId) {
        List<TransactionDto> transactions = bailService.getTransactions(bailId);
        return ResponseEntity.ok(transactions);
    }

    @PutMapping("/{bailId}/sortie")
    public ResponseEntity<BailDto> sortirLocataire(@PathVariable Long bailId) {
        BailDto updated = bailService.sortirLocataire(bailId);
        return ResponseEntity.ok(updated);
    }

}
