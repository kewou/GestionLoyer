package com.example.features.bail;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.mapper.LoyerDto;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.bail.dto.BailDto;
import com.example.features.bail.dto.CreateBailRequestDto;
import com.example.features.transaction.Transaction;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class BailService {

    @Autowired
    private BailRepository bailRepository;

    @Autowired
    private AppartRepository appartRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private final BailMapper bailMapper;

    public BailService(BailMapper bailMapper) {
        this.bailMapper = bailMapper;
    }

    public Bail creerBail(Appart appart, Client locataire, LocalDate dateEntree) {
        Bail bail = new Bail();
        bail.setAppart(appart);
        bail.setLocataire(locataire);
        bail.setDateEntree(dateEntree);
        return bailRepository.save(bail);
    }

    public Bail cloturerBail(Bail bail, LocalDate dateSortie) {
        bail.setDateSortie(dateSortie);
        return bailRepository.save(bail);
    }

    public Optional<Bail> getBailActuel(Appart appart) {
        return bailRepository.findByAppartAndDateSortieIsNull(appart);
    }


    public List<LoyerDto> getHistoriqueLoyers(Long bailId) {
        Bail bail = bailRepository.findById(bailId)
                .orElseThrow(() -> new IllegalArgumentException("Bail introuvable"));

        List<LoyerDto> loyers = new ArrayList<>();

        LocalDate start = bail.getDateEntree().withDayOfMonth(1);
        LocalDate end = bail.getDateSortie() != null ? bail.getDateSortie().withDayOfMonth(1) : LocalDate.now().withDayOfMonth(1);

        // Récupère toutes les transactions
        List<Transaction> transactions = transactionRepository.findByBail(bail);

        // Somme totale versée
        int totalVerse = transactions.stream()
                .mapToInt(Transaction::getMontant)
                .sum();

        int montantLoyer = bail.getAppart().getPrixLoyer();
        LocalDate current = start;

        // Distribuer les loyers jusqu'à la fin du bail ou jusqu'à ce qu'on dépasse aujourd'hui
        while (!current.isAfter(end) || totalVerse >= montantLoyer) {
            int montantAttendu = montantLoyer;

            int montantVerse;
            boolean ok;

            if (totalVerse >= montantAttendu) {
                // Loyer payé (ou en avance)
                montantVerse = montantAttendu;
                ok = true;
                totalVerse -= montantAttendu;
            } else {
                // Partiellement payé ou pas payé
                montantVerse = totalVerse;
                ok = montantVerse >= montantAttendu;
                totalVerse = 0;
            }

            loyers.add(LoyerDto.builder()
                    .mois(current)
                    .montantAttendu(montantAttendu)
                    .montantVerse(montantVerse)
                    .ok(ok)
                    .courant(current.equals(LocalDate.now().withDayOfMonth(1)))
                    .build());

            current = current.plusMonths(1);

            // Si plus de versement et on a dépassé la date courante → on s'arrête
            if (totalVerse == 0 && current.isAfter(LocalDate.now().withDayOfMonth(1))) {
                break;
            }
        }

        return loyers;
    }


    public BailDto assignLocataire(String refAppart, CreateBailRequestDto request) throws BusinessException {
        Appart appart = appartRepository.findByReference(refAppart)
                .orElseThrow(() -> new BusinessException("Appartement introuvable"));

        // Vérifie qu’il n’y a pas déjà un bail actif
        Optional<Bail> actif = bailRepository.findByAppartAndDateSortieIsNull(appart);
        if (actif.isPresent()) {
            throw new BusinessException("Cet appartement a déjà un locataire actif");
        }

        // Récupère le locataire
        Client locataire = clientRepository.findByReference(request.getLocataireRef())
                .orElseThrow(() -> new BusinessException("Locataire introuvable"));

        // Crée le bail
        Bail bail = new Bail();
        bail.setAppart(appart);
        bail.setLocataire(locataire);
        bail.setDateEntree(request.getDateEntree());
        bail.setDateSortie(request.getDateSortiePrevue());
        bail.setActif(true);

        Bail saved = bailRepository.save(bail);

        return bailMapper.dto(saved);
    }

    public List<LoyerDto> createTransaction(Long bailId, Integer montant) {
        Bail bail = bailRepository.findById(bailId)
                .orElseThrow(() -> new IllegalArgumentException("Bail introuvable"));

        // 1. Créer et sauvegarder la transaction
        Transaction tx = new Transaction();
        tx.setBail(bail);
        tx.setMontant(montant);
        tx.setDate(LocalDate.now()); // ou LocalDateTime.now()
        transactionRepository.save(tx);

        // 2. Retourner l’historique mis à jour
        return getHistoriqueLoyers(bailId);
    }

    public BailDto sortirLocataire(Long bailId) {
        Bail bail = bailRepository.findById(bailId)
                .orElseThrow(() -> new IllegalArgumentException("Bail introuvable"));

        // Met fin au bail
        bail.setDateSortie(LocalDate.now());
        bail.setActif(false);

        Bail saved = bailRepository.save(bail);
        return bailMapper.dto(saved);
    }


}
