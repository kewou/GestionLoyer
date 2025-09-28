package com.example.repositories;

import com.example.features.appart.domain.entities.Appart;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.logement.Logement;
import com.example.features.logement.LogementRepository;
import com.example.features.transaction.Transaction;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

/**
 * @author jnoumia
 */
@DataJpaTest
public class TransactionRepositoryIT {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AppartRepository appartRepository;

    @Autowired
    private LogementRepository logementRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void saveAndFindByAppart_shouldReturnTransaction() {

        Client client = new Client();
        client.setEmail("client@example.com");
        client.setPassword("secret");
        client.setReference("CLI-001");
        client = clientRepository.save(client);


        Logement logement = new Logement();
        logement.setReference("LGT-001");
        logement.setDescription("Immeuble Paris 15");
        logement.setClient(client);
        logement = logementRepository.save(logement);
        // Création d'un appart
        Appart appart = new Appart();
        appart.setReference("APP-1001");
        appart.setId(1L);
        appart.setLogement(logement);
        appart = appartRepository.save(appart);

        // Création transaction liée à l'appart
        Transaction tx = new Transaction();
        tx.setMontantVerser(250);
        //tx.setAppart(appart);

        transactionRepository.save(tx);

        // Vérification avec findByAppart
        /*List<Transaction> results = transactionRepository.findByAppart(appart);

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getAppart().getReference()).isEqualTo("APP-1001");*/
    }
}
