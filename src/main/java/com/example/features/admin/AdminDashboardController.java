package com.example.features.admin;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.bail.Bail;
import com.example.features.bail.BailRepository;
import com.example.features.bail.dto.BailDto;
import com.example.features.bail.BailMapper;
import com.example.features.logement.Logement;
import com.example.features.logement.LogementDto;
import com.example.features.logement.LogementRepository;
import com.example.features.payment.entity.CampayTransaction;
import com.example.features.payment.repository.CampayTransactionRepository;
import com.example.features.transaction.Transaction;
import com.example.features.transaction.TransactionDto;
import com.example.features.transaction.TransactionMapper;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.security.SecurityRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller admin pour la gestion globale de la plateforme.
 */
@RestController
@RequestMapping("/admin")
@PreAuthorize(SecurityRule.ADMIN)
public class AdminDashboardController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private LogementRepository logementRepository;

    @Autowired
    private AppartRepository appartRepository;

    @Autowired
    private BailRepository bailRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CampayTransactionRepository campayTransactionRepository;

    @Autowired
    private TransactionMapper transactionMapper;

    @Autowired
    private BailMapper bailMapper;

    // ===== Dashboard stats =====

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", clientService.getAllClient().size());
        stats.put("totalLogements", logementRepository.count());
        stats.put("totalApparts", appartRepository.count());
        stats.put("totalBaux", bailRepository.count());
        stats.put("totalTransactions", transactionRepository.count());
        stats.put("totalCampayTransactions", campayTransactionRepository.count());
        return ResponseEntity.ok(stats);
    }

    // ===== Logements =====

    @GetMapping("/logements")
    public ResponseEntity<List<AdminLogementDto>> getAllLogements() {
        List<Logement> logements = logementRepository.findAll();
        List<AdminLogementDto> dtos = logements.stream().map(l -> {
            AdminLogementDto dto = new AdminLogementDto();
            dto.setReference(l.getReference());
            dto.setQuartier(l.getQuartier());
            dto.setVille(l.getVille());
            dto.setDescription(l.getDescription());
            dto.setBailleurName(l.getClient().getName() + " " + l.getClient().getLastName());
            dto.setBailleurReference(l.getClient().getReference());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/logements/{reference}")
    public ResponseEntity<Void> deleteLogement(@PathVariable String reference) {
        logementRepository.deleteByReference(reference);
        return ResponseEntity.noContent().build();
    }

    // ===== Appartements =====

    @GetMapping("/apparts")
    public ResponseEntity<List<AdminAppartDto>> getAllApparts() {
        List<Appart> apparts = appartRepository.findAll();
        List<AdminAppartDto> dtos = apparts.stream().map(a -> {
            AdminAppartDto dto = new AdminAppartDto();
            dto.setReference(a.getReference());
            dto.setNom(a.getNom());
            dto.setPrixLoyer(a.getPrixLoyer());
            dto.setPrixCaution(a.getPrixCaution());
            dto.setLogementReference(a.getLogement().getReference());
            dto.setLogementDescription(a.getLogement().getQuartier() + " - " + a.getLogement().getVille());
            dto.setBailleurReference(a.getLogement().getClient().getReference());
            dto.setBailleurName(a.getLogement().getClient().getName() + " " + a.getLogement().getClient().getLastName());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/apparts/{reference}")
    public ResponseEntity<Void> deleteAppart(@PathVariable String reference) {
        appartRepository.deleteByReference(reference);
        return ResponseEntity.noContent().build();
    }

    // ===== Baux =====

    @GetMapping("/baux")
    public ResponseEntity<List<AdminBailDto>> getAllBaux() {
        List<Bail> baux = bailRepository.findAll();
        List<AdminBailDto> dtos = baux.stream().map(b -> {
            AdminBailDto dto = new AdminBailDto();
            dto.setId(b.getId());
            dto.setLocataireName(b.getLocataire().getName() + " " + b.getLocataire().getLastName());
            dto.setLocataireReference(b.getLocataire().getReference());
            dto.setAppartNom(b.getAppart().getNom());
            dto.setAppartReference(b.getAppart().getReference());
            dto.setLogementReference(b.getAppart().getLogement().getReference());
            dto.setDateEntree(b.getDateEntree() != null ? b.getDateEntree().toString() : null);
            dto.setDateSortie(b.getDateSortie() != null ? b.getDateSortie().toString() : null);
            dto.setActif(b.getActif());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    // ===== Transactions (versements) =====

    @GetMapping("/transactions")
    public ResponseEntity<List<AdminTransactionDto>> getAllTransactions() {
        List<Transaction> transactions = transactionRepository.findAll();
        List<AdminTransactionDto> dtos = transactions.stream().map(t -> {
            AdminTransactionDto dto = new AdminTransactionDto();
            dto.setId(t.getId());
            dto.setMontant(t.getMontant());
            dto.setDate(t.getDate() != null ? t.getDate().toString() : null);
            dto.setBailId(t.getBail().getId());
            dto.setAppartNom(t.getBail().getAppart().getNom());
            dto.setAppartReference(t.getBail().getAppart().getReference());
            dto.setLocataireName(t.getBail().getLocataire().getName() + " " + t.getBail().getLocataire().getLastName());
            dto.setLocataireReference(t.getBail().getLocataire().getReference());
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Transactions Campay =====

    @GetMapping("/campay-transactions")
    public ResponseEntity<List<CampayTransaction>> getAllCampayTransactions() {
        return ResponseEntity.ok(campayTransactionRepository.findAll());
    }
}

