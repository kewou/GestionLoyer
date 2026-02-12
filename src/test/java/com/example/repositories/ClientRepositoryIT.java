package com.example.repositories;

import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author jnoumia
 */
@DataJpaTest
public class ClientRepositoryIT {

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void saveAndFindByEmail_shouldReturnClient() {
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setReference("REF-123");
        client.setPassword("secret");
        clientRepository.save(client);

        Client found = clientRepository.findByEmail("test@example.com");

        assertThat(found).isNotNull();
        assertThat(found.getReference()).isEqualTo("REF-123");
    }

    @Test
    void saveAndFindByReference_shouldReturnClient() {
        Client client = new Client();
        client.setEmail("john@example.com");
        client.setReference("REF-999");
        client.setPassword("1234");
        clientRepository.save(client);

        Optional<Client> found = clientRepository.findByReference("REF-999");

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void deleteByReference_shouldRemoveClient() {
        Client client = new Client();
        client.setEmail("delete@example.com");
        client.setReference("DEL-001");
        client.setPassword("pwd");
        clientRepository.save(client);

        clientRepository.deleteByReference("DEL-001");

        Optional<Client> found = clientRepository.findByReference("DEL-001");
        assertThat(found).isEmpty();
    }
}
