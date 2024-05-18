package com.example;

import com.example.domain.dto.ClientDto;
import com.example.repository.ClientRepository;
import com.example.services.impl.ClientService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
public class AllTestIntegrationTest {

    private static final String URL = "/users";

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;


    @BeforeEach
    public void setUp() {
        clientRepository.deleteAll();
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void getAllUserTest() throws Exception {
        mockMvc.perform(get(URL).
                        contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void createUserTest() throws Exception {
        String val = mapper.writeValueAsString(ClientDto.builder()
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(clientRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)

                        .content(val))
                .andDo(print())
                .andExpect(status().is(HttpStatus.CREATED.value()));
        Assertions.assertEquals(clientRepository.findAll().size(), 1);
    }

    @Test
    public void getUserByReferenceTest() throws Exception {
        String val = mapper.writeValueAsString(ClientDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(clientRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL + "/create")
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
    public void updateUserTest() throws Exception {
        String valPost = mapper.writeValueAsString(ClientDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(clientRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL + "/create")
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
    public void deleteUserTest() throws Exception {
        String val = mapper.writeValueAsString(ClientDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(clientRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL + "/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(val));
        Assertions.assertEquals(clientRepository.findAll().size(), 1);
        String ref = clientRepository.findAll().iterator().next().getReference();
        this.mockMvc.perform(delete(URL + "/" + ref))
                .andExpect(status().is(HttpStatus.NO_CONTENT.value()));
        Assertions.assertTrue(clientRepository.findAll().isEmpty());

    }

    /*
    @Test
    public void testLogement() {
        String email = "kewou.noumia@gmail.com";
        User user = userService.getUserByEmail(email);
        assertEquals(user.getLogements().size(), 1);
    }

    @Test
    public void testRecapLogement() {
        String email = "kewou.noumia@gmail.com";
        User user = userService.getUserByEmail(email);
        assertEquals(user.getLogements().iterator().next().getRecapByMonths().size(), 1);
    }
    */

}
