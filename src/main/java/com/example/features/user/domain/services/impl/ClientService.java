package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.common.mail.MessageDto;
import com.example.features.common.mail.MessageService;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ClientMapper;
import com.example.features.user.application.mapper.UpdatePasswordDto;
import com.example.features.user.application.mapper.UserInfoDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.security.Role;
import com.example.utils.GeneralUtils;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.*;

/**
 * @author kewou
 */
@Service
@Slf4j
@Transactional
public class ClientService implements ClientAppService {

    private final ClientRepository clientRepository;

    private final MessageService messageService;

    private final ClientMapper clientMapper;

    @Value("${inscription.message:}")
    private String inscriptionMessage;

    @Value("${inscription.validation.uri:https://beezyweb.net}")
    private String uriSite;

    @Value("${inscription.sender:}")
    private String inscriptionSender;

    @Value("${resetPassword.message:}")
    private String resetPasswordMessage;

    public ClientService(ClientRepository clientRepository, MessageService messageService, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.messageService = messageService;
        this.clientMapper = clientMapper;
    }

    public List<ClientDto> getAllClient() {
        return clientRepository.findAll().stream()
                .map(clientMapper::dto)
                .collect(Collectors.toList());
    }

    public ClientDto register(ClientDto clientDto, Role clientRole) throws BusinessException {
        Client client = clientMapper.entitie(clientDto);
        initializeClientCollections(client);
        if (!checkIfClientExist(client.getEmail())) {
            if (Objects.equals(client.getReference(), "") || client.getReference() == null) {
                client.setReference(GeneralUtils.generateReference());
            }
            client.setPassword(encoder.encode(client.getPassword()));
            Set<String> roles = new HashSet<>();
            roles.add(clientRole.name());
            client.setRoles(roles);
            clientRepository.save(client);
            log.info("Client {} is created ", client.getReference());
            sendInscriptionMail(client);
            return clientMapper.dto(client);
        } else {
            throw new BusinessException(
                    String.format("Client with email %s is already exist on database", client.getEmail()), OTHER);
        }
    }

    public ClientDto register(ClientDto clientDto) throws BusinessException {
        Client client = clientMapper.entitie(clientDto);
        initializeClientCollections(client);
        if (!checkIfClientExist(client.getEmail())) {
            if (Objects.equals(client.getReference(), "") || client.getReference() == null) {
                client.setReference(GeneralUtils.generateReference());
            }
            Set<String> roles = new HashSet<>();
            roles.add(Role.BAILLEUR.name());
            client.setRoles(roles);
            client.setIsEnabled(true);
            clientRepository.save(client);
            log.info("Client {} is created ", client.getReference());
            sendInscriptionMail(client);
            return clientMapper.dto(client);
        } else {
            throw new BusinessException(
                    String.format("Client with email %s is already exist on database", client.getEmail()), OTHER);
        }
    }

    public void sendInscriptionMail(Client client) {
        final String verificationToken = client.getVerificationToken() != null ? client.getVerificationToken()
                : "token_generated";
        String message;
        if (inscriptionMessage != null && !inscriptionMessage.isBlank()) {
            String activationLink = String.format("%s/login#%s/%s", uriSite, client.getReference(), verificationToken);
            try {
                message = String.format(
                        inscriptionMessage,
                        String.format("%s %s", client.getName(), client.getLastName()),
                        activationLink,
                        uriSite
                );
            } catch (IllegalFormatException ex) {
                log.warn("Invalid inscription.message template, using fallback message", ex);
                message = "Votre compte a ete cree. Veuillez contacter le support en cas de probleme d'activation.";
            }
        } else {
            message = "Votre compte a ete cree.";
        }
        log.debug("Mail {}", message);
        try {
            messageService.sendHtmlMessage(
                    MessageDto.builder()
                            .subject("Beezyweb : Validation de votre compte")
                            .sender(inscriptionSender != null && !inscriptionSender.isBlank() ? inscriptionSender : "noreply@beezyweb.net")
                            .recipients(List.of(client.getEmail()))
                            .message(message)
                            .build());
        } catch (MessagingException e) {
            log.error("Error sendind email", e);
        }
    }

    @Override
    public void sendResetPasswordMail(Client client) throws BusinessException {
        if (Boolean.TRUE.equals(client.getIsVirtual())) {
            throw new BusinessException("Aucun compte n'est associe a cet e-mail.", NOT_FOUND);
        }
        final String email = client.getEmail();
        final String message;
        String verificationToken = GeneralUtils.generateVerificationToken();
        String resetLink = String.format("%s/password-reset?email=%s#%s", uriSite, email, verificationToken);
        client.setVerificationToken(verificationToken);
        if (resetPasswordMessage != null && !resetPasswordMessage.isBlank()) {
            try {
                message = String.format(
                        resetPasswordMessage,
                        String.format("%s %s", client.getName(), client.getLastName()),
                        resetLink,
                        resetLink,
                        uriSite
                );
            } catch (IllegalFormatException ex) {
                log.warn("Invalid resetPassword.message template, using fallback message", ex);
                throw new BusinessException(
                        "Cette fonctionnalite ne fonctionne pas pour le moment. Veuillez reessayer plus tard.",
                        INTERNAL_SERVER_ERROR
                );
            }
        } else {
            message = "Demande de reinitialisation de mot de passe";
        }
        try {
            messageService.sendHtmlMessage(
                    MessageDto.builder()
                            .subject("BeezyWeb : Reinitialisation de mot de passe")
                            .sender(inscriptionSender != null && !inscriptionSender.isBlank() ? inscriptionSender : "noreply@beezyweb.net")
                            .recipients(List.of(email))
                            .message(message)
                            .build());
        } catch (MessagingException | RuntimeException e) {
            log.error("Error sending reset password email", e);
            throw new BusinessException("Impossible d'envoyer l'email de reinitialisation pour le moment.", INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updatePasswordClient(UpdatePasswordDto updatePasswordDto) throws BusinessException {
        Client client = clientRepository.findByEmail(updatePasswordDto.getEmail());
        if (client == null) {
            throw new BusinessException("Cette fonctionnalite ne fonctionne pas pour le moment. Veuillez reessayer plus tard.");
        }
        client.setPassword(encoder.encode(updatePasswordDto.getPassword()));
        clientRepository.save(client);
    }

    public ClientDto getClientByReference(String reference) throws BusinessException {
        return clientMapper.dto(this.getClientFromDatabase(reference));
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public ClientDto update(ClientDto clientDto, String reference) throws BusinessException {
        Client client = this.getClientFromDatabase(reference);
        Client clientUpdate = clientMapper.entitie(clientDto);
        initializeClientCollections(clientUpdate);
        clientMapper.update(client, clientUpdate);
        clientRepository.save(client);
        log.info("Client {} is update ", reference);
        return clientMapper.dto(clientUpdate);
    }

    public void delete(String reference) throws BusinessException {
        Client client = this.getClientFromDatabase(reference);
        clientRepository.delete(client);
        log.info("Client {} is delete ", reference);
    }

    public Client getClientFromDatabase(String reference) throws BusinessException {
        Client client = clientRepository.findByReference(reference)
                .orElseThrow(() -> new BusinessException(
                        String.format("No user found with this reference %s", reference), NOT_FOUND));
        log.info("Client {} is found ", reference);
        return client;
    }

    public Client getClientWithBaux(String reference) throws BusinessException {
        Client client = clientRepository.findByReferenceWithBaux(reference)
                .orElseThrow(() -> new BusinessException(
                        String.format("No user found with this reference %s", reference), NOT_FOUND));
        log.info("Client {} is found with {} baux", reference, client.getBaux() != null ? client.getBaux().size() : 0);
        return client;
    }

    @Override
    public UserInfoDto getUserRole(UserInfoDto userInfoDto) throws BusinessException {
        Set<String> roles = getClientByEmail(userInfoDto.getEmail()).getRoles();
        Iterator<String> iterator = roles.iterator();
        userInfoDto.setRole(iterator.next());
        return userInfoDto;
    }

    @Override
    public void validateToken(Client client) {
        client.setVerificationToken(null);
        client.setIsEnabled(true);
        clientRepository.save(client);
    }

    @Override
    public List<ClientDto> searchLocatairesByName(String name, String bailleurEmail) {
        String bailleurRef = null;
        if (bailleurEmail != null) {
            Client bailleur = clientRepository.findByEmail(bailleurEmail);
            if (bailleur != null) {
                bailleurRef = bailleur.getReference();
            }
        }
        return clientRepository.searchLocatairesByName(name, bailleurRef).stream()
                .map(clientMapper::dto)
                .collect(Collectors.toList());
    }

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private boolean checkIfClientExist(String email) {
        return clientRepository.findByEmail(email) != null;
    }

    private void initializeClientCollections(Client client) {
        if (client != null) {
            if (client.getRoles() == null) {
                client.setRoles(new HashSet<>());
            }
            if (client.getLogements() == null) {
                client.setLogements(new HashSet<>());
            }
            if (client.getBaux() == null) {
                client.setBaux(new HashSet<>());
            }
        }
    }

}
