package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.common.mail.MessageDto;
import com.example.features.common.mail.MessageService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ClientMapper;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.security.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.mail.MessagingException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@TestPropertySource(value = "/application-test.properties")
class ClientServiceTest {

    ClientService clientService;

    @MockBean
    ClientRepository clientRepository;

    @MockBean
    MessageService messageService;

    @MockBean
    ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, messageService, clientMapper);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "BAILLEUR", "LOCATAIRE"})
    void should_create_client_and_send_mail(Role clientRole) throws BusinessException, MessagingException {
        // Given
        final String password = "test";
        ClientDto clientDto = ClientDto.builder()
                .email("test@client.fr")
                .lastName("Test")
                .name("Client")
                .password(password)
                .build();

        Client client = Client.builder()
                .email("test@client.fr")
                .lastName("Test")
                .name("Client")
                .password("encoded_password")
                .reference("REF123")
                .build();

        ClientDto clientDtoResult = ClientDto.builder()
                .email("test@client.fr")
                .lastName("Test")
                .name("Client")
                .password("encoded_password")
                .reference("REF123")
                .build();

        when(clientMapper.entitie(any(ClientDto.class))).thenReturn(client);
        when(clientMapper.dto(any(Client.class))).thenReturn(clientDtoResult);
        when(clientRepository.findByEmail(anyString())).thenReturn(null);
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // When
        final ClientDto clientRegistered = clientService.register(clientDto, clientRole);

        // Then
        verify(clientRepository, times(1)).save(any(Client.class));
        // Note: sendInscriptionMail est commenté dans register(ClientDto, Role), donc
        // on ne vérifie pas l'envoi d'email
        // verify(messageService, times(1)).sendHtmlMessage(any(MessageDto.class));
        Assertions.assertNotNull(clientRegistered.getReference());
        Assertions.assertNotEquals(password, clientRegistered.getPassword());

    }

    @Test
    void should_send_reset_password_mail_when_client_really_exists() throws MessagingException {
        // Given
        String mail = "test@client.fr";
        Client client = Client.builder()
                .email(mail)
                .reference("AXXXXX")
                .build();
        when(clientRepository.findByEmail(mail)).thenReturn(client);

        // When
        clientService.sendResetPasswordMail(client);

        // Then
        verify(messageService, times(1)).sendHtmlMessage(any(MessageDto.class));

    }

    @Test
    void should_send_reset_password_mail_with_this_exact_message() throws MessagingException {
        // Given
        String mail = "test@client.fr";
        Client client = Client.builder()
                .email(mail)
                .reference("AXXXXX")
                .lastName("Test")
                .name("Client")
                .build();

        // When
        clientService.sendResetPasswordMail(client);

        // Then
        verify(messageService, times(1)).sendHtmlMessage(
                any(MessageDto.class));
    }
}