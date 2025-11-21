package com.example.controllers;

import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.services.impl.ClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class ClientControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(authorities = "ADMIN")
    void searchLocataires_returnsListOfClients() throws Exception {
        // given
        ClientDto locataire1 = new ClientDto("ref1", "Dupont", "test", "dupont@mail.com", "3", "pass1");
        ClientDto locataire2 = new ClientDto("ref2", "Durand", "test2", "durand@mail.com", "4", "pass2");

        Mockito.when(clientService.searchLocatairesByName(anyString()))
                .thenReturn(List.of(locataire1, locataire2));

        // when/then
        mockMvc.perform(get("/users/ref1/search")
                        .param("name", "Du")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Dupont"))
                .andExpect(jsonPath("$[1].name").value("Durand"));
    }
}
