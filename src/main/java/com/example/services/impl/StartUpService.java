package com.example.services.impl;

import com.example.domain.entities.Logement;
import com.example.domain.entities.RecapByMonth;
import com.example.domain.entities.User;
import com.example.repository.LogementRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

//@Service
public class StartUpService implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LogementRepository logementRepository;

    @Override
    public void run(String... args) throws Exception {


        //Insertion des données pour les tests

        // Un user : Role Admin
        User admin = new User();
        admin.setName("Joel");
        admin.setLastName("beezy");
        admin.setEmail("kewou.noumia@gmail.com");
        admin.setRole("Admin");
        userRepository.save(admin);
        // Un user : Role Proprio
        User proprio = new User();
        proprio.setName("Kidou");
        proprio.setLastName("Dorine");
        proprio.setEmail("dorisclam@yahoo.fr");
        proprio.setRole("Proprio");

        // Un logement : Nkomkana
        Logement nkomkana = new Logement(300000, "Jean-Mermoz", "Duplex en bord de route", proprio);
        nkomkana.setUser(proprio);
        Set<Logement> lgts = new HashSet<Logement>();
        lgts.add(nkomkana);

        // Un recap de Nkomkana
        RecapByMonth recapNkomkana = new RecapByMonth(new Date(), 100000, 50000, nkomkana);
        Set<RecapByMonth> recaps = new HashSet<RecapByMonth>();
        recaps.add(recapNkomkana);

        nkomkana.setRecapByMonths(recaps);
        proprio.setLogements(lgts);
        userRepository.save(proprio);

    }
}
