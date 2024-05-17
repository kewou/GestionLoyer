package com.example.services.impl;

import com.example.domain.entities.Appart;
import com.example.domain.entities.Client;
import com.example.domain.entities.Loyer;
import com.example.domain.entities.Transaction;
import com.example.domain.exceptions.BusinessException;
import com.example.repository.LoyerRepository;
import com.example.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

import static com.example.domain.exceptions.BusinessException.BusinessErrorType.OTHER;

@Service
@Transactional
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LoyerRepository loyerRepository;


    public Transaction register(Client bailleur, Transaction transaction, Appart appart) throws BusinessException {
        int prixLoyer = appart.getPrixLoyer().intValue();
        if (transaction.getMontantVerser() < prixLoyer) {
            throw new BusinessException("Le montant de la transaction doit etre supérieux au prix du loyer !", OTHER);
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
        return transaction;
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
        /*
        // On paye les loyers à partir du mois pivot : loyer.getNbMoisPayer() + 1 = 0 = mois courant
        int soldeRestant = transaction.getMontantVerser();
        int indiceMoisLoyer = 0;
        int montantLoyer = appart.getPrixLoyer().intValue();
        while (nbLoyerPayer >= 1) {
            Optional<Loyer> anotherLoyerOptional = loyerRepository.findByMonthAndAppart(loyer.getNbMoisPayer() + 1 + indiceMoisLoyer + moisActuel, appart);
            if (anotherLoyerOptional.isPresent()) {
                // On a trouvé un Loyer, donc c'est un retard de paiement, on met l'objet à jour
                Loyer loyerTrouver = anotherLoyerOptional.get();
                if (!loyerTrouver.getIsOk()) {
                    loyerTrouver.setSolde(loyerTrouver.getSolde() + soldeRestant);
                    loyerTrouver.setNbMoisPayer(loyerTrouver.getNbMoisPayer() + 1);
                    loyerTrouver.setIsOk(true);
                    loyerRepository.save(loyerTrouver);
                }
            } else {
                // On a pas trouvé un Loyer, c'est un paiement en avance, on crée l'objet
                Loyer newLoyer = new Loyer(appart);
                newLoyer.setDateLoyer(dateActuelle.plusMonths(indiceMoisLoyer + 1));
                int newSolde = soldeRestant - montantLoyer;
                newLoyer.setSolde(newSolde);
                newLoyer.setIsOk(true);
                newLoyer.setNbMoisPayer(newLoyer.getNbMoisPayer() + soldeRestant / montantLoyer);
                loyerRepository.save(newLoyer);
            }
            indiceMoisLoyer++;
            nbLoyerPayer--;
            soldeRestant = soldeRestant - montantLoyer;

        }
        transactionRepository.save(transaction);
        return transaction;
    }*/


}
