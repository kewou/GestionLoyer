package com.example;

import com.example.features.appart.application.mapper.AppartDto;
import com.example.features.appart.infra.AppartRepository;
import com.example.features.bail.dto.CreateBailRequestDto;
import com.example.features.common.mail.MessageService;
import com.example.features.logement.LogementDto;
import com.example.features.logement.LogementRepository;
import com.example.features.transaction.TransactionDto;
import com.example.features.transaction.TransactionRepository;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.features.user.infra.ClientRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        mockMvc.perform(get("/admin/users").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserTest() throws Exception {
        String val = mapper.writeValueAsString(ClientDto.builder()
                .reference("refUser")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
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
        // String ref = clientRepository.findAll().iterator().next().getReference();
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
                .nom("355")
                .reference("refAppart")
                .prixLoyer(500)
                .prixCaution(200)
                .build());
        Assertions.assertTrue(appartRepository.findAll().isEmpty());

        mockMvc.perform(post("/bailleur/users/refUser/logements/refLgt/apparts/create")
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

        String res = mockMvc.perform(get("/bailleur/users/refUser/logements/refLgt/apparts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res).get(0);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "355");
        // Note: Le champ "loyers" n'existe plus dans AppartDto, il est maintenant dans
        // BailDto
        // Vérifier que l'appartement a été créé correctement
        Assertions.assertNotNull(resNode.get("reference"));
        Assertions.assertEquals(resNode.get("prixLoyer").asInt(), 500);

    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void getOneAppartByLogementTest() throws Exception {
        createAppartTest();

        String res = mockMvc.perform(get("/bailleur/users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "355");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void updateOneAppartByLogementTest() throws Exception {
        createAppartTest();

        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("nom", 400);
        String valPut = mapper.writeValueAsString(m);

        mockMvc.perform(put("/bailleur//users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(valPut))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        String res = mockMvc.perform(get("/bailleur//users/refUser/logements/refLgt/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();

        JsonNode resNode = mapper.readerForMapOf(Object.class).readTree(res);
        String nom = resNode.get("nom").asText();
        Assertions.assertEquals(nom, "400");
    }

    @Test
    @WithMockUser(authorities = "ADMIN")
    public void mettreLocataireDansAppartTest() throws Exception {
        createAppartTest();

        // Créer un locataire d'abord
        String locataireDto = mapper.writeValueAsString(ClientDto.builder()
                .reference("refLocataire")
                .name("LOCATAIRE")
                .lastName("TEST")
                .email("locataire.test@gmail.com")
                .phone("0615664758")
                .password("Tourneyuvbekuyb*155r14")
                .build());
        mockMvc.perform(post(URL + "/create-locataire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(locataireDto));

        // Assigner le locataire à l'appartement via le nouvel endpoint
        CreateBailRequestDto bailDto = new CreateBailRequestDto();
        bailDto.setLocataireRef("refLocataire");
        bailDto.setDateEntree(java.time.LocalDate.now());
        String bailRequestJson = mapper.writeValueAsString(bailDto);

        mockMvc.perform(post("/baux/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bailRequestJson))
                .andExpect(status().is(HttpStatus.CREATED.value()));

        String res = mockMvc.perform(get("/bailleur/users/refUser/logements/refLgt/apparts/refAppart")
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

        // Créer un locataire d'abord
        String locataireDto = mapper.writeValueAsString(ClientDto.builder()
                .reference("refLocataireTx")
                .name("LOCATAIRE")
                .lastName("TX")
                .email("locataire.tx@gmail.com")
                .phone("0615664758")
                .password("Tourneyuvbekuyb*155r14")
                .build());
        mockMvc.perform(post(URL + "/create-locataire")
                .contentType(MediaType.APPLICATION_JSON)
                .content(locataireDto));

        // Assigner le locataire à l'appartement pour créer un bail
        CreateBailRequestDto bailDto = new CreateBailRequestDto();
        bailDto.setLocataireRef("refLocataireTx");
        bailDto.setDateEntree(java.time.LocalDate.now());
        String bailRequestJson = mapper.writeValueAsString(bailDto);

        String bailResponse = mockMvc.perform(post("/baux/apparts/refAppart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bailRequestJson))
                .andExpect(status().is(HttpStatus.CREATED.value()))
                .andReturn().getResponse().getContentAsString();

        // Récupérer le bailId de la réponse
        JsonNode bailNode = mapper.readerForMapOf(Object.class).readTree(bailResponse);
        Long bailId = bailNode.get("id").asLong();

        // Créer une transaction via le nouvel endpoint
        String transaction = mapper.writeValueAsString(TransactionDto.builder()
                .montant(500)
                .build());
        Assertions.assertTrue(transactionRepository.findAll().isEmpty());

        mockMvc.perform(post("/baux/" + bailId + "/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transaction))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Assertions.assertEquals(transactionRepository.findAll().size(), 1);
    }

}
