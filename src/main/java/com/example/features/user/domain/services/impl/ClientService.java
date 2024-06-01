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
import com.example.features.user.application.mapper.UserInfoDto;
import com.example.features.user.domain.entities.Client;
import com.example.features.user.infra.ClientRepository;
import com.example.security.Role;
import com.example.utils.GeneralUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
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
            return ClientMapper.getMapper().dto(client);
        } else {
            throw new BusinessException(String.format("Client with email %s is already exist on database", client.getEmail()), OTHER);
        }
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


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private boolean checkIfClientExist(String email) {
        return clientRepository.findByEmail(email) != null;
    }

}
