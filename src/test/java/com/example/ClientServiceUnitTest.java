package com.example;

import com.example.domain.entities.Client;
import com.example.domain.exceptions.NoClientFoundException;
import com.example.domain.mapper.ClientMapper;
import com.example.repository.ClientRepository;
import com.example.services.impl.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ClientServiceUnitTest {

    @MockBean
    private ClientRepository clientRepository;

    @Autowired
    private ClientService clientService;

    @Test
    public void createClientTest() throws RuntimeException {
        Client client = new Client("NOUMIA");
        client.setLastName("Joel");
        client.setPassword("test");

        when(clientRepository.save(client)).thenReturn(client);
        Client createdClient = clientService.register(client);

        Assertions.assertEquals("NOUMIA", createdClient.getName());
        Assertions.assertEquals("Joel", createdClient.getLastName());

    }


    @Test
    public void getOneClientByReferenceTest() throws NoClientFoundException {
        Client client = new Client("Joel");
        client.setId(1L);
        client.setLastName("kewou");
        client.setReference("ref_1");

        when(clientRepository.findByReference("ref_1")).thenReturn(Optional.of(client));
        Client foundClient = clientService.getClientByReference("ref_1");
        Assertions.assertNotNull(foundClient);
        Assertions.assertEquals("Joel", foundClient.getName());
        Assertions.assertEquals("kewou", foundClient.getLastName());
    }

    @Test
    public void getAllClientTest() {
        Client client1 = new Client("Joel");
        client1.setId(1L);
        client1.setLastName("kewou");
        client1.setReference("ref_1");
        Client client2 = new Client("JP");
        client2.setId(2L);
        client2.setLastName("babasco");
        client2.setReference("ref_2");
        List<Client> clients = Arrays.asList(client1, client2);

        when(clientRepository.findAll()).thenReturn(clients);
        List<Client> foundClients = clientService.getAllClient();
        Assertions.assertEquals(2, foundClients.size());
        Assertions.assertEquals("kewou", foundClients.get(0).getLastName());
        Assertions.assertEquals("babasco", foundClients.get(1).getLastName());
    }

    @Test
    public void updateClientTest() throws NoClientFoundException {
        Client client = new Client("Joel");
        client.setReference("ref_1");
        Client updateClientDetails = new Client();
        updateClientDetails.setName("Beezy");

        when(clientRepository.findByReference("ref_1")).thenReturn(Optional.of(client));
        when(clientRepository.save(client)).thenReturn(client);

        Client updateClient = clientService.update(ClientMapper.getMapper().dto(updateClientDetails), "ref_1");

        Assertions.assertEquals("Beezy", client.getName());

    }

    @Test
    public void deleteClientTest() throws NoClientFoundException {
        Client client = new Client("Joel");
        client.setId(1L);
        client.setLastName("kewou");
        client.setReference("ref_1");

        when(clientRepository.findByReference("ref_1")).thenReturn(Optional.of(client));

        clientService.delete("ref_1");

        verify(clientRepository, times(1)).delete(client);
    }


}
