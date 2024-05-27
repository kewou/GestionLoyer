/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.features.user.domain.services.impl;

import com.example.exceptions.BusinessException;
import com.example.features.user.application.appService.ClientAppService;
import com.example.features.user.application.mapper.ClientDto;
import com.example.features.user.application.mapper.ClientMapper;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.exceptions.BusinessException.BusinessErrorType.NOT_FOUND;
import static com.example.exceptions.BusinessException.BusinessErrorType.OTHER;

/**
 * @author frup73532
 */
@Service
@Slf4j
@Transactional
public class ClientService implements ClientAppService {

    private ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> getAllClient() {
        return clientRepository.findAll();
    }

    public Client register(Client client, String clientRole) throws BusinessException {
        if (!checkIfClientExist(client.getEmail())) {
            if (client.getReference() == null) {
                client.setReference(GeneralUtils.generateReference());
            }
            client.setPassword(encoder.encode(client.getPassword()));
            Set<String> roles = new HashSet<>();
            roles.add(clientRole);
            client.setRoles(roles);
            clientRepository.save(client);
            return client;
        } else {
            throw new BusinessException(String.format("Client with email %s is already exist on database", client.getEmail()), OTHER);
        }
    }


    public Client getClient(Long id) throws BusinessException {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(String.format("No user found with this id %d", id), NOT_FOUND));
        log.info("Client {} is found  ", client.getEmail());
        return client;
    }


    public Client getClientByReference(String reference) throws BusinessException {
        Client client = clientRepository.findByReference(reference)
                .orElseThrow(() -> new BusinessException(String.format("No user found with this reference %s", reference), NOT_FOUND));
        log.info("Client {} is found ", client.getEmail());
        return client;
    }

    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }


    public Client update(ClientDto clientDto, String reference) throws BusinessException {
        Client client = getClientByReference(reference);
        Client clientUpdate = ClientMapper.getMapper().entitie(clientDto);
        ClientMapper.getMapper().update(client, clientUpdate);
        clientRepository.save(client);
        return client;
    }

    public Client delete(String reference) throws BusinessException {
        Client client = getClientByReference(reference);
        log.info("Client {} is found ", reference);
        clientRepository.delete(client);
        return client;
    }

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private boolean checkIfClientExist(String email) {
        return clientRepository.findByEmail(email) != null;
    }


}
