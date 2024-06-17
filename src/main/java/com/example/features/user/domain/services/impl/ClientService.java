/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.common.mail.application.MessageService;
import com.example.features.common.mail.dto.MessageDto;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ClientMapper;
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

    @Value("${inscription.message}")
    private String inscriptionMessage;

    @Value("${inscription.validation.uri:https://beezyweb.net/login}")
    private String uriSite;

    @Value("${inscription.sender}")
    private String inscriptionSender;

    public ClientService(ClientRepository clientRepository, MessageService messageService) {
        this.clientRepository = clientRepository;
        this.messageService = messageService;
    }

    public List<ClientDto> getAllClient() {
        return clientRepository.findAll().stream()
                .map(ClientMapper.getMapper()::dto)
                .collect(Collectors.toList());
    }


    public ClientDto register(ClientDto clientDto, Role clientRole) throws BusinessException {
        Client client = ClientMapper.getMapper().entitie(clientDto);
        if (!checkIfClientExist(client.getEmail())) {
            if (client.getReference() == null) {
                client.setReference(GeneralUtils.generateReference());
            }
            client.setPassword(encoder.encode(client.getPassword()));
            Set<String> roles = new HashSet<>();
            roles.add(clientRole.name());
            client.setRoles(roles);
            clientRepository.save(client);
            log.info("Client {} is created ", client.getReference());
            sendInscriptionMail(client);
            return ClientMapper.getMapper().dto(client);
        } else {
            throw new BusinessException(String.format("Client with email %s is already exist on database", client.getEmail()), OTHER);
        }
    }

    private void sendInscriptionMail(Client client) {
        final String verificationToken = client.getVerificationToken() != null ? client.getVerificationToken() : "token_generated";
        final String message = inscriptionMessage != null ? String.format(inscriptionMessage,
                String.format("%s %s", client.getName(), client.getLastName()),
                String.format("%s#%s/%s", uriSite,
                        client.getReference(),
                        verificationToken),
                "https://beezyweb.net") : "Vous êtes bien inscrits";
        log.debug("Mail {}", message);
        messageService.sendMessage(
                MessageDto.builder()
                        .subject("Beezyweb : Validation de votre compte")
                        .sender(inscriptionSender)
                        .recipients(List.of(client.getEmail()))
                        .message(message)
                        .build()
        );
    }


    public ClientDto getClientByReference(String reference) throws BusinessException {
        return ClientMapper.getMapper().dto(this.getClientFromDatabase(reference));
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    public ClientDto update(ClientDto clientDto, String reference) throws BusinessException {
        Client client = this.getClientFromDatabase(reference);
        Client clientUpdate = ClientMapper.getMapper().entitie(clientDto);
        ClientMapper.getMapper().update(client, clientUpdate);
        clientRepository.save(client);
        log.info("Client {} is update ", reference);
        return ClientMapper.getMapper().dto(clientUpdate);
    }

    public void delete(String reference) throws BusinessException {
        Client client = this.getClientFromDatabase(reference);
        clientRepository.delete(client);
        log.info("Client {} is delete ", reference);
    }

    public Client getClientFromDatabase(String reference) throws BusinessException {
        Client client = clientRepository.findByReference(reference)
                .orElseThrow(() -> new BusinessException(String.format("No user found with this reference %s", reference), NOT_FOUND));
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


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private boolean checkIfClientExist(String email) {
        return clientRepository.findByEmail(email) != null;
    }

}
