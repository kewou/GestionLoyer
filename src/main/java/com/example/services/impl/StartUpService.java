package com.example.services.impl;

import com.example.entities.Logement;
import com.example.entities.User;
import com.example.repository.LogementRepository;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.Set;

@Service
public class StartUpService implements CommandLineRunner {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LogementRepository logementRepository;

    @Override
    public void run(String... args) throws Exception {

        /*
           Insertion des données pour les tests

        User admin = new User();
        admin.setName("Joel");
        admin.setLastName("beezy");
        admin.setEmail("kewou.noumia@gmail.com");
        admin.setRole("Admin");
        userRepository.save(admin);

        User proprio =new User();
        proprio.setName("Kidou");
        proprio.setLastName("Dorine");
        proprio.setEmail("dorisclam@yahoo.fr");
        proprio.setRole("Proprio");
        userRepository.save(proprio);

        Logement nkomkana = new Logement(300000,"Jean-Mermoz","Duplex en bord de route",proprio);

        Set<Logement> lgts = new HashSet<Logement>();
        lgts.add(nkomkana);
        proprio.setLogements(lgts);
        userRepository.save(proprio);
        */

    }
}
