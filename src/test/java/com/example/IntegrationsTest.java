package com.example;

import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.common.mail.application.MessageService;
import com.example.features.logement.application.mapper.LogementDto;
import com.example.features.logement.infra.LogementRepository;
import com.example.features.transaction.application.mapper.TransactionDto;
import com.example.features.transaction.infra.TransactionRepository;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.features.user.infra.ClientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class IntegrationsTest {

    private static final String URL = "/users";

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LogementRepository logementRepository;

    @Autowired
    private AppartRepository appartRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    @Qualifier("loggingMailService")
    MessageService messageService;


    @BeforeEach
    public void setUp() {
        logementRepository.deleteAll();
        clientRepository.deleteAll();
        appartRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getAllUserTest() throws Exception {
        mockMvc.perform(get("/admin/users").
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserTest() throws Exception {
        String val = mapper.writeValueAsString(ClientDto.builder()
                .reference("refUser")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("Tourneyuvbekuyb*155r14")
                .build());
        Assertions.assertTrue(clientRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL + "/create-locataire")
                        .contentType(MediaType.APPLICATION_JSON)

                        .content(val))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Assertions.assertEquals(clientRepository.findAll().size(), 1);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getUserByReferenceTest() throws Exception {
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
        Assertions.assertEquals(clientRepository.findAll().size(), 1);
        String res = this.mockMvc.perform(get(URL + "/test_id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String idRef = resNode.get("reference").asText();
        Assertions.assertEquals(idRef, "test_id");

    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateUserTest() throws Exception {
        String valPost = mapper.writeValueAsString(ClientDto.builder()
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
                .content(valPost));
        Assertions.assertEquals(clientRepository.findAll().size(), 1);
        //String ref = clientRepository.findAll().iterator().next().getReference();
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("reference", "test_id");
        m.put("name", "KEWOU");
        String valPut = mapper.writeValueAsString(m);
        this.mockMvc.perform(put(URL + "/test_id").contentType(MediaType.APPLICATION_JSON)
                        .content(valPut))
                .andExpect(status().is(HttpStatus.OK.value()));
        String res = this.mockMvc.perform(get(URL + "/test_id")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        String name = mapper.readerForMapOf(Object.class).readTree(res).get("name").asText();
        Assertions.assertEquals(name, "KEWOU");
    }


    @Test
    @Transactional
    @WithMockUser(authorities = "ADMIN")
    public void deleteUserTest() throws Exception {
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
        Assertions.assertEquals(clientRepository.findAll().size(), 1);
        String ref = clientRepository.findAll().iterator().next().getReference();
        this.mockMvc.perform(delete("/admin/users/" + ref))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        Assertions.assertTrue(clientRepository.findAll().isEmpty());

    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createLogementTest() throws Exception {
        this.createUserTest();
        String logement = mapper.writeValueAsString(LogementDto.builder()
                .quartier("Nkomkana")
                .ville("Yaoundé")
                .description("immeuble")
                .reference("refLgt")
                .build());
        Assertions.assertTrue(logementRepository.findAll().isEmpty());

        mockMvc.perform(post("/bailleur/users/refUser/logements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logement))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()));
        Assertions.assertEquals(logementRepository.findAll().size(), 1);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getLogementsByUserTest() throws Exception {
        createLogementTest();


        String res = mockMvc.perform(get("/bailleur/users/refUser/logements")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(logementRepository.findAll().size(), 1);
        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res).get(0);
        String idRef = resNode.get("description").asText();
        Assertions.assertEquals(idRef, "immeuble");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getOneLogementByUserTest() throws Exception {
        createLogementTest();


        String res = mockMvc.perform(get("/bailleur/users/refUser/logements/refLgt")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        Assertions.assertEquals(logementRepository.findAll().size(), 1);
        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String description = resNode.get("description").asText();
        Assertions.assertEquals(description, "immeuble");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateOneLogementByUserTest() throws Exception {
        createLogementTest();

        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("description", "Duplex");
        String valPut = mapper.writeValueAsString(m);

        mockMvc.perform(put("/bailleur/users/refUser/logements/refLgt")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valPut))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        String res = mockMvc.perform(get("/bailleur/users/refUser/logements/refLgt")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();


        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String description = resNode.get("description").asText();
        Assertions.assertEquals(description, "Duplex");
    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createAppartTest() throws Exception {
        this.createLogementTest();
        String appart = mapper.writeValueAsString(AppartDto.builder()
                .nom("beezyAppart")
                .reference("refAppart")
                .prixLoyer(500)
                .prixCaution(200)
                .build());
        Assertions.assertTrue(appartRepository.findAll().isEmpty());

        mockMvc.perform(post(URL + "/refUser/logements/refLgt/apparts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(appart))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()));
        Assertions.assertEquals(appartRepository.findAll().size(), 1);
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getListAppartByLogementTest() throws Exception {
        createAppartTest();

        String res = mockMvc.perform(get("/users/refUser/logements/refLgt/apparts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res).get(0);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "beezyAppart");
        // test génération automatique du premier loyer
        JsonNode loyers = resNode.get("loyers");
        Assertions.assertEquals(loyers.size(), 1);
        JsonNode loyer = loyers.get(0);
        Assertions.assertEquals(loyer.get("solde"), new IntNode(-500)); // 500 étant le prix du loyer de l'appart créer


    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getOneAppartByLogementTest() throws Exception {
        createAppartTest();

        String res = mockMvc.perform(get("/users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "beezyAppart");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateOneAppartByLogementTest() throws Exception {
        createAppartTest();

        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("nom", "JoelAppart");
        String valPut = mapper.writeValueAsString(m);

        mockMvc.perform(put("/users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valPut))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        String res = mockMvc.perform(get("/users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "JoelAppart");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void mettreLocataireDansAppartTest() throws Exception {
        createAppartTest();

        mockMvc.perform(patch("/users/refUser/logements/refLgt/apparts/refAppart/nouveau-locataire/refUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        String res = mockMvc.perform(get("/users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        Assertions.assertNotNull(resNode);
    }


    @Test
    @WithMockUser(authorities = "ADMIN")
    public void createTransactionTest() throws Exception {
        this.createAppartTest();
        String transaction = mapper.writeValueAsString(TransactionDto.builder()
                .montantVerser(500)
                .build());
        Assertions.assertTrue(transactionRepository.findAll().isEmpty());

        mockMvc.perform(post(URL + "/refUser/apparts/refAppart/nouvelle-transaction")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transaction))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()));
        Assertions.assertEquals(transactionRepository.findAll().size(), 1);

        // Test de l'effet de la transaction sur l'appartement

        String res = mockMvc.perform(get("/users/refUser/logements/refLgt/apparts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res).get(0);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "beezyAppart");
        // test génération automatique du premier loyer
        JsonNode loyers = resNode.get("loyers");
        Assertions.assertEquals(loyers.size(), 1);
        JsonNode loyer = loyers.get(0);
        Assertions.assertEquals(loyer.get("solde"), new IntNode(0));


    }

}
