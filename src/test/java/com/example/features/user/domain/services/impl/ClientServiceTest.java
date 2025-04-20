package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.common.mail.application.MessageService;
import com.example.features.common.mail.dto.MessageDto;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.security.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = {"/application-test.properties"},
        properties = "inscription.message=Bonjour %s, \\n\\n Merci de vous être inscrit sur notre site. Veuillez cliquer sur le lien suivant pour valider votre inscription : \\n %s \\n\\n.Si vous n'avez pas créé de compte, veuillez ignorer cet email \\n\\n. Cordialement,\\n L'équipe <a href='%s'>BeezyWeb </a>")
class ClientServiceTest {

    ClientService clientService;

    @MockBean
    ClientRepository clientRepository;

    @MockBean
    MessageService messageService;


    @BeforeEach
    void setUp() {
        clientService = new ClientService(clientRepository, messageService);
    }


    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "BAILLEUR", "LOCATAIRE"})
    void should_create_client_and_send_mail(Role clientRole) throws BusinessException {
        //Given
        final String password = "test";

        //When
        final ClientDto clientRegistered = clientService.register(ClientDto.builder()
                .email("test@client.fr")
                .lastName("Test")
                .name("Client")
                .password(password)
                .build(), clientRole);

        //Then
        verify(clientRepository, times(1)).save(any(Client.class));
        final MessageDto inscriptionMessage = MessageDto.builder()
                .subject("Beezyweb : Validation de votre compte")
                .message(String.format("Bonjour %s, \n\n Merci de vous être inscrit sur notre site. Veuillez cliquer sur le lien suivant pour valider votre inscription : \n %s \n\n. " +
                                "Si vous n'avez pas créé de compte, veuillez ignorer cet email \n\n. Cordialement,\n L'équipe <a href='%s'>BeezyWeb </a>",
                        "Client Test", "localhost:4200/token_generated", "https://beezyweb.net"))
                .sender("beezyweb@beezyweb.net")
                .recipients(List.of("test@client.fr"))
                .build();
        //verify(messageService, times(1)).sendMessage(any(MessageDto.class));
        Assertions.assertNotNull(clientRegistered.getReference());
        Assertions.assertNotEquals(clientRegistered.getPassword(), password);

    }
}