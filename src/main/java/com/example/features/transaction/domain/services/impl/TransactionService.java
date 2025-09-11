package com.example.features.transaction.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.appart.application.appService.AppartAppService;
import com.example.features.appart.domain.entities.Appart;
import com.example.features.loyer.domain.entities.Loyer;
import com.example.features.loyer.infra.LoyerRepository;
import com.example.features.transaction.application.appService.TransactionAppService;
import com.example.features.transaction.application.mapper.TransactionDto;
import com.example.features.transaction.application.mapper.TransactionMapper;
import com.example.features.transaction.domain.entities.Transaction;
import com.example.features.transaction.infra.TransactionRepository;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.domain.entities.Client;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.OTHER;

@Service
@Slf4j
@Transactional
public class TransactionService implements TransactionAppService {

    private final ClientAppService clientAppService;
    private final AppartAppService appartAppService;
    private final TransactionRepository transactionRepository;
    private final LoyerRepository loyerRepository;

    @Autowired
    public TransactionService(ClientAppService clientAppService, TransactionRepository transactionRepository,
                              AppartAppService appartAppService, LoyerRepository loyerRepository) {
        this.appartAppService = appartAppService;
        this.clientAppService = clientAppService;
        this.transactionRepository = transactionRepository;
        this.loyerRepository = loyerRepository;
    }

    public List<TransactionDto> getAllTransactionByAppart(String refAppart) throws BusinessException {
        Appart appart = appartAppService.getAppartFromDatabase(refAppart);
        return transactionRepository.findByAppart(appart).stream()
                .map(TransactionMapper.getMapper()::dto)
                .collect(Collectors.toList());
    }


    public TransactionDto register(String refClient, TransactionDto transactionDto, String refAppart) throws BusinessException {
        Client bailleur = clientAppService.getClientFromDatabase(refClient);
        Appart appart = appartAppService.getAppartFromDatabase(refAppart);
        Transaction transaction = TransactionMapper.getMapper().entitie(transactionDto);
        int prixLoyer = appart.getPrixLoyer().intValue();
        int montantTransaction = transaction.getMontantVerser();
        if (montantTransaction < prixLoyer) {
            throw new BusinessException("Le montant de la transaction doit etre supérieux au prix du loyer : " + prixLoyer + "! ", OTHER);
        }
        // Obtenir la date actuelle
        LocalDate dateActuelle = LocalDate.now();
        // Obtenir le mois de la date actuelle
        int moisActuel = dateActuelle.getMonthValue();
        transaction.setBailleur(bailleur);
        transaction.setAppart(appart);
        int nbLoyerPayer = transaction.getMontantVerser() / prixLoyer;
        transaction.setNbLoyerPayer(nbLoyerPayer);
        transaction.setDate(dateActuelle);
        transaction.setReference(GeneralUtils.generateReference());

        // Recupère l objet loyer du mois courant (existe forcément)
        Loyer loyerCourant = loyerRepository.findByMonthAndAppart(moisActuel, appart).get();
        if (!loyerCourant.isOk()) {
            // Le loyer n'est pas payé, on va régulariser tous les loyers impayer possible
            List<Loyer> loyerImpayerList = loyerRepository.findByIsKo(appart);
            int nbLoyerImpayer = loyerImpayerList.size();
            Loyer loyerPlusAncien = getLoyerLePlusAncien(loyerImpayerList);
            // Je régularise le loyer le plus ancien
            loyerPlusAncien.setIsOk(true);
            loyerPlusAncien.setSolde(loyerPlusAncien.getSolde() + montantTransaction);
            montantTransaction = montantTransaction - prixLoyer;
            nbLoyerImpayer--;
            while (montantTransaction > 0) { // on paye les impayés tant qu'il ya l'argent
                while (nbLoyerImpayer > 0) {
                    loyerCourant = getLoyerMoisSuivant(loyerImpayerList, loyerPlusAncien);
                    loyerCourant.setIsOk(true);
                    loyerCourant.setSolde(loyerPlusAncien.getSolde() + montantTransaction);
                    loyerRepository.save(loyerCourant);
                    loyerPlusAncien = loyerCourant;
                    nbLoyerImpayer--;
                    montantTransaction = montantTransaction - prixLoyer;
                }
                // On a fini de payer les impayés, maintenant on paye de nouveaux loyers
                Loyer newLoyer = new Loyer();
                newLoyer.setDateLoyer(loyerCourant.getDateLoyer().plusMonths(1));
                newLoyer.setAppart(appart);
                newLoyer.setIsOk(true);
                newLoyer.setSolde(loyerCourant.getSolde() - prixLoyer);
                loyerRepository.save(newLoyer);
                loyerCourant = newLoyer;
                montantTransaction = montantTransaction - prixLoyer;
            }
        } else {
            // Le loyer est payé,obtenir le dernier loyer payé afin de payer les suivants
            List<Loyer> tousLesLoyers = loyerRepository.findByAppart(appart);
            Loyer dernierLoyer = getLoyerLePlusVieu(tousLesLoyers);
            int dernierSolde = dernierLoyer.getSolde() + montantTransaction;
            for (int i = 0; i < nbLoyerPayer; i++) {
                Loyer newLoyer = new Loyer();
                newLoyer.setDateLoyer(dernierLoyer.getDateLoyer().plusMonths(i + 1));
                newLoyer.setAppart(appart);
                newLoyer.setIsOk(true);
                dernierSolde = dernierSolde - prixLoyer;
                newLoyer.setSolde(dernierSolde);
                loyerRepository.save(newLoyer);
            }
        }
        transactionRepository.save(transaction);
        log.info("Transaction du montant : " + montantTransaction + " :ok ");
        return TransactionMapper.getMapper().dto(transaction);
    }

    private Loyer getLoyerLePlusAncien(List<Loyer> loyers) throws BusinessException {
        if (!loyers.isEmpty()) {
            LocalDate datePlusAncienne = loyers.get(0).getDateLoyer();
            Loyer loyerPlusAncien = loyers.get(0);
            for (Loyer loyer : loyers) {
                if (loyer.getDateLoyer().isBefore(datePlusAncienne)) {
                    datePlusAncienne = loyer.getDateLoyer();
                    loyerPlusAncien = loyer;
                }
            }
            return loyerPlusAncien;
        } else {
            throw new BusinessException("Au moins un loyer non payé", OTHER);
        }
    }

    private Loyer getLoyerLePlusVieu(List<Loyer> loyers) throws BusinessException {
        if (!loyers.isEmpty()) {
            LocalDate datePlusVielle = loyers.get(0).getDateLoyer();
            Loyer loyerPlusVieu = loyers.get(0);
            for (Loyer loyer : loyers) {
                if (loyer.getDateLoyer().isAfter(datePlusVielle)) {
                    datePlusVielle = loyer.getDateLoyer();
                    loyerPlusVieu = loyer;
                }
            }
            return loyerPlusVieu;
        } else {
            throw new BusinessException("Au moins un loyer doit etre trouver", OTHER);
        }
    }

    private Loyer getLoyerMoisSuivant(List<Loyer> loyers, Loyer loyerCourant) throws BusinessException {
        // Calculer le mois suivant
        int moisActuel = loyerCourant.getDateLoyer().getMonthValue();
        int moisSuivant = (moisActuel == 12) ? 1 : moisActuel + 1;
        for (Loyer loyer : loyers) {
            if (loyer.getDateLoyer().getMonthValue() + 1 == moisSuivant) {
                return loyer;
            }
        }
        throw new BusinessException("Cette recherche doit retourner un objet !", OTHER);
    }


}
