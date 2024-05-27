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

import static com.example.exceptions.BusinessException.BusinessErrorType.OTHER;

@Service
@Slf4j
@Transactional
public class TransactionService implements TransactionAppService {

    private ClientAppService clientAppService;
    private AppartAppService appartAppService;
    private TransactionRepository transactionRepository;
    private LoyerRepository loyerRepository;

    @Autowired
    public TransactionService(ClientAppService clientAppService, TransactionRepository transactionRepository,
                              AppartAppService appartAppService, LoyerRepository loyerRepository) {
        this.appartAppService = appartAppService;
        this.clientAppService = clientAppService;
        this.transactionRepository = transactionRepository;
        this.loyerRepository = loyerRepository;
    }


    public TransactionDto register(String refClient, TransactionDto transactionDto, String refAppart) throws BusinessException {
        Client bailleur = clientAppService.getClientFromDatabase(refClient);
        Appart appart = appartAppService.getAppartFromDatabase(refAppart);
        Transaction transaction = TransactionMapper.getMapper().entitie(transactionDto);
        int prixLoyer = appart.getPrixLoyer().intValue();
        if (transaction.getMontantVerser() < prixLoyer) {
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

        // Reccupère l objet loyer du mois courant (existe forcément)
        Loyer loyerCourant = loyerRepository.findByMonthAndAppart(moisActuel, appart).get();
        int montantTransaction = transaction.getMontantVerser();
        if (!loyerCourant.isOk()) {
            // Le loyer n'est pas payé, on va régulariser tous les loyers impayer possible
            List<Loyer> loyerImpayerList = loyerRepository.findByIsKo(appart);
            Loyer loyerPlusAncien = getLoyerLePlusAncien(loyerImpayerList);
            // Je régularise le loyer le plus ancien
            loyerPlusAncien.setIsOk(true);
            loyerPlusAncien.setSolde(loyerPlusAncien.getSolde() + montantTransaction);
            for (int i = 1; i < nbLoyerPayer; i++) {
                Loyer loyerSuivant = getLoyerMoisSuivant(loyerImpayerList, loyerPlusAncien);
                loyerSuivant.setIsOk(true);
                loyerSuivant.setSolde(loyerPlusAncien.getSolde() + montantTransaction);
                loyerRepository.save(loyerSuivant);
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
