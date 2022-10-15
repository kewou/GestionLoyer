package com.example;

import com.example.domain.dto.UserDto;
import com.example.domain.entities.User;
import com.example.repository.UserRepository;
import com.example.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class UserRepositoryTestIT {

    private static final String URL = "/users";

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mockMvc;


    @BeforeEach
    public void setUp() {userRepository.deleteAll();}

    @Test
    void contextLoads() {}

    @Test
    public void getAllUserTest() throws Exception{
        mockMvc.perform(get(URL).
                contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void createUserTest() throws Exception {
        String val = mapper.writeValueAsString(UserDto.builder()
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(userRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(val))
                .andDo(print())
                .andExpect(status().is(HttpStatus.OK.value()));
        Assertions.assertEquals(userRepository.findAll().size(),1);
    }

    @Test
    public void getUserTest() throws Exception {
        String val = mapper.writeValueAsString(UserDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(userRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(val));
        Assertions.assertEquals(userRepository.findAll().size(),1);
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
        String valPost = mapper.writeValueAsString(UserDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(userRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(valPost));
        Assertions.assertEquals(userRepository.findAll().size(),1);
        String ref = userRepository.findAll().iterator().next().getReference();
        HashMap<String, Object> m = new HashMap<String, Object>();
        m.put("reference", "referenceUpdate");
        String valPut = mapper.writeValueAsString(m);
        this.mockMvc.perform(put(URL + "/" + ref).contentType(MediaType.APPLICATION_JSON)
                        .content(valPut))
                .andExpect(status().is(HttpStatus.OK.value()));
        String res = this.mockMvc.perform(get(URL + "/referenceUpdate")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(HttpStatus.OK.value()))
                .andReturn().getResponse().getContentAsString();
        String reference = mapper.readerForMapOf(Object.class).readTree(res).get("reference").asText();
        Assertions.assertNotEquals(reference, "test_id");
    }


    @Test
    @Transactional
    public void deleteRouteProfileTest() throws Exception {
        String val = mapper.writeValueAsString(UserDto.builder()
                .reference("test_id")
                .name("NOUMIA")
                .lastName("joel")
                .email("kewou.noumia@gmail.com")
                .phone("0615664758")
                .password("sbeezy12")
                .build());
        Assertions.assertTrue(userRepository.findAll().isEmpty());
        this.mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(val));
        Assertions.assertEquals(userRepository.findAll().size(),1);
        String ref = userRepository.findAll().iterator().next().getReference();
        this.mockMvc.perform(delete(URL + "/" + ref))
                .andExpect(status().is(HttpStatus.OK.value()));
        Assertions.assertTrue(userRepository.findAll().isEmpty());

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
