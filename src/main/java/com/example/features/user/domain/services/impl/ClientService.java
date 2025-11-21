/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;
import static com.example.exceptions.BusinessException.BusinessErrorType.OTHER;

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

    @Value("${inscription.message}")
    private String inscriptionMessage;

    @Value("${inscription.validation.uri:https://beezyweb.net}")
    private String uriSite;

    @Value("${inscription.sender}")
    private String inscriptionSender;

    @Value("${resetPassword.message}")
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
            // sendInscriptionMail(client);
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
        final String message = inscriptionMessage != null ? String.format(inscriptionMessage,
                String.format("%s %s", client.getName(), client.getLastName()),
                String.format("%s/login#%s/%s", uriSite,
                        client.getReference(),
                        verificationToken),
                uriSite) : "Vous êtes bien inscrits";
        log.debug("Mail {}", message);
        try {
            messageService.sendHtmlMessage(
                    MessageDto.builder()
                            .subject("Beezyweb : Validation de votre compte")
                            .sender(inscriptionSender)
                            .recipients(List.of(client.getEmail()))
                            .message(message)
                            .build());
        } catch (MessagingException e) {
            log.error("Error sendind email", e);
        }
    }

    @Override
    public void sendResetPasswordMail(Client client) {
        final String email = client.getEmail();
        final String message;
        String verificationToken = GeneralUtils.generateVerificationToken();
        client.setVerificationToken(verificationToken);
        if (resetPasswordMessage != null) {
            message = String.format(resetPasswordMessage,
                    String.format("%s %s", client.getName(), client.getLastName()),
                    String.format("%s/password-reset?email=%s#%s", uriSite,
                            email,
                            verificationToken),
                    uriSite);
        } else {
            message = "Mot de passe oublié";
        }
        try {
            messageService.sendHtmlMessage(
                    MessageDto.builder()
                            .subject("BeezyWeb : Réinitialisation de mot de passe")
                            .sender(inscriptionSender)
                            .recipients(List.of(email))
                            .message(message)
                            .build());
        } catch (MessagingException e) {
            log.error("Error sending email", e);
        }
    }

    @Override
    public void updatePasswordClient(UpdatePasswordDto updatePasswordDto) throws BusinessException {
        Client client = clientRepository.findByEmail(updatePasswordDto.getEmail());
        if (client == null) {
            throw new BusinessException("Client not found");
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
    public List<ClientDto> searchLocatairesByName(String name) {
        return clientRepository.searchLocatairesByName(name).stream()
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
