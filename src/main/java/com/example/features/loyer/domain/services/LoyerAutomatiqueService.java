package com.example.features.loyer.domain.services;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.loyer.domain.entities.Loyer;
import com.example.features.loyer.infra.LoyerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LoyerAutomatiqueService {

    private AppartRepository appartRepository;
    private LoyerRepository loyerRepository;

    @Autowired
    public LoyerAutomatiqueService(AppartRepository appartRepository, LoyerRepository loyerRepository) {
        this.appartRepository = appartRepository;
        this.loyerRepository = loyerRepository;
    }


    @Scheduled(cron = "0 0 0 1 * *") // Exécuter à minuit (00:00:00) le 1er jour de chaque mois
    //@Scheduled(cron = "0 */1 * * * *") // Exécuter toutes les une minute ( pour tester )
    public void genereLoyerMensuel() {

        List<Appart> apparts = appartRepository.findAll();

        // Obtenir la date actuelle
        LocalDate dateActuelle = LocalDate.now();
        // Obtenir le mois de la date actuelle
        int moisActuel = dateActuelle.getMonthValue();
        // Calculer le mois précédent
        int moisPrecedent = (moisActuel == 1) ? 12 : moisActuel - 1;

        // Version avec possibilité de mettre des locataires
        /*
        for (Appart appart : apparts) {
            //  L'appatement est vide : Créer un Loyer vide
            if (appart.getLocataire() == null) {
                Loyer newLoyerVide = new Loyer(appart);
                newLoyerVide.setDateLoyer(dateActuelle);
                loyerRepository.save(newLoyerVide);
            } else {
                // Check si le loyer a été payé via une Transaction
                Optional<Loyer> loyer = loyerRepository.findByMonthAndAppart(moisActuel, appart);
                if (loyer.isEmpty()) {
                    // Loyer du mois non payé : mise à jour du solde.
                    Loyer precedentLoyer = loyerRepository.findByMonthAndAppart(moisPrecedent, appart).get();
                    // il ya forcément un précédent car la création d'un appart se fait au premier paiement du loyer
                    Loyer newLoyerImpayer = new Loyer(precedentLoyer.getAppart());
                    newLoyerImpayer.setDateLoyer(dateActuelle);
                    int newSolde = precedentLoyer.getSolde() - appart.getPrixLoyer();
                    newLoyerImpayer.setSolde(newSolde);
                    loyerRepository.save(newLoyerImpayer);
                }
                // On ne fait rien, le loyer du mois a été payé par une transaction
            }
        }*/
        // Version sans les locataires
        for (Appart appart : apparts) {
            // Check si le loyer a été payé via une Transaction
            Optional<Loyer> loyer = loyerRepository.findByMonthAndAppart(moisActuel, appart);
            if (loyer.isEmpty()) {
                // Loyer du mois non payé : mise à jour du solde.
                Loyer precedentLoyer = loyerRepository.findByMonthAndAppart(moisPrecedent, appart).get();
                // il ya forcément un précédent car la création d'un appart se fait au premier paiement du loyer
                Loyer newLoyerImpayer = new Loyer(precedentLoyer.getAppart());
                newLoyerImpayer.setDateLoyer(dateActuelle);
                int newSolde = precedentLoyer.getSolde() - appart.getPrixLoyer();
                newLoyerImpayer.setSolde(newSolde);
                loyerRepository.save(newLoyerImpayer);
            }
            // On ne fait rien, le loyer du mois a été payé par une transaction
        }

    }
}
