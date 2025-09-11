package com.example.controllers;

import com.example.exceptions.BusinessException;
import com.example.features.accueil.domain.services.AuthenticationService;
import com.example.features.transaction.application.mapper.TransactionDto;
import com.example.features.transaction.domain.services.impl.TransactionService;
import org.junit.jupiter.api.BeforeEach;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author jnoumia
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class TransactionControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @MockBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() throws BusinessException {
        Mockito.when(authenticationService.isOwnerBailleurAppart(anyString(), anyString())).thenReturn(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTransactionOfOneAppartOf_shouldReturnTransactions() throws Exception {
        // Préparer des transactions mockées
        TransactionDto dto = new TransactionDto();
        dto.setReference("TXN-001");
        dto.setMontantVerser(250);

        Mockito.when(transactionService.getAllTransactionByAppart(anyString()))
                .thenReturn(List.of(dto));

        // Appeler l’API
        mockMvc.perform(get("/bailleur/users/CLI-001/logements/LGT-001/apparts/APP-1001/transactions")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].montantVerser").value(250));

    }
}
