package com.example;

import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.utils.jwt.JwtRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class GetTokenForAuthTest {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    private static final String URL = "/users";

    @BeforeEach
    public void setUp() {
        // Nettoyer la base avant chaque test
        clientRepository.deleteAll();
    }

    @Test
    @Transactional
    public void getUserByEmailTest() throws Exception {
        String val = mapper.writeValueAsString(ClientDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("Tourneyuvbekuyb*155r14")
                .build());
        Assertions.assertTrue(clientRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL + "/create-locataire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(val));
        Assertions.assertEquals(1, clientRepository.findAll().size());
        Client client = clientRepository.findByEmail("kewou.noumia@gmail.com");
        Assertions.assertNotNull(client);
        Assertions.assertEquals("NOUMIA", client.getName());

    }

    @Test
    public void authenticationTest() throws Exception {
        // Créer un utilisateur pour le test d'authentification
        String val = mapper.writeValueAsString(ClientDto.builder()
                .reference("test_auth_id")
                .name("AUTH")
                .lastName("TEST")
                .email("auth.test@gmail.com")
                .phone("0615664758")
                .password("Tourneyuvbekuyb*155r14")
                .build());
        this.mockMvc.perform(post(URL + "/create-locataire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(val));

        // Activer le compte
        Client client = clientRepository.findByEmail("auth.test@gmail.com");
        if (client != null) {
            client.setEnabled(true);
            clientRepository.save(client);
        }

        String body = mapper.writeValueAsString(JwtRequest.builder()
                .username("auth.test@gmail.com")
                .password("Tourneyuvbekuyb*155r14")
                .build());

        // Requête POST en JSON
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

}
