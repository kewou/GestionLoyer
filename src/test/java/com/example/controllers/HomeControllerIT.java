package com.example.controllers;

import com.example.features.user.domain.entities.Client;
import com.example.features.user.domain.services.impl.ClientService;
import com.example.utils.JWTUtils;
import com.example.utils.jwt.JwtRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author jnoumia
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class HomeControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private ClientService clientService;

    @MockBean
    private JWTUtils jwtUtils;

    @Test
    void authenticate_returnsJwt_onValidCredentials() throws Exception {
        // given
        JwtRequest body = new JwtRequest();
        body.setUsername("user@example.com");
        body.setPassword("secret");

        Authentication authResult =
                new UsernamePasswordAuthenticationToken("user@example.com", "secret");
        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authResult);

        Client client = new Client();
        client.setId(1L);
        client.setEmail("user@example.com");
        Mockito.when(clientService.getClientByEmail("user@example.com")).thenReturn(client);

        Mockito.when(jwtUtils.generateToken(client)).thenReturn("dummy.jwt.token");

        // when/then
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // on vérifie que le token est présent dans la réponse
                .andExpect(jsonPath("$.jwtToken").value("dummy.jwt.token"));
    }

    @Test
    void authenticate_returns401_onBadCredentials() throws Exception {
        // given
        JwtRequest body = new JwtRequest();
        body.setUsername("user@example.com");
        body.setPassword("wrong");

        Mockito.when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("bad"));

        // when/then
        mockMvc.perform(post("/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("INVALID CREDENTIAL"));
    }


}
