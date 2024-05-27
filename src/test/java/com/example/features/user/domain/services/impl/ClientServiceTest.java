package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@Import(ClientService.class)
@ExtendWith(SpringExtension.class)
class ClientServiceTest {

    ClientService clientService;

    @MockBean
    ClientRepository clientRepository;


    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository);
    }

    @ParameterizedTest
    @ValueSource(strings = {"LOCATAIRE", "BAILLEUR", "ADMIN"})
    void should_create_client(String clientRole) throws BusinessException {
        //Given
        final String password = "test";

        //When
        final Client clientRegistered = clientService.register(Client.builder()
                .email("test@client.fr")
                .lastName("Test")
                .name("Client")
                .phone("0654768954")
                .password(password)
                .build(), clientRole);

        //Then
        verify(clientRepository, times(1)).save(any(Client.class));
        assertThat(clientRegistered.getReference()).isNotNull();
        assertThat(clientRegistered.getPassword()).isNotEqualTo(password);
        assertThat(clientRegistered.getRoles()).contains(clientRole);

    }
}